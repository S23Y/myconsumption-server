package org.starfishrespect.myconsumption.server.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.DayStat;
import org.starfishrespect.myconsumption.server.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.DayStatRepository;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 02.04.15.
 * Tool that computes the statistics based on latest data. It also saves the statistics in the database.
 */
public class StatisticsUpdater {
    @Autowired
    private SensorRepository mSensorRepository;
    @Autowired
    private StatRepository mStatRepository;
    @Autowired
    private DayStatRepository mDayStatRepository;

    private final Logger mLogger = LoggerFactory.getLogger(StatisticsUpdater.class);

    public StatisticsUpdater(SensorRepository seRepo, StatRepository stRepo, DayStatRepository dStRepo) {
        this.mSensorRepository = seRepo;
        this.mStatRepository = stRepo;
        this.mDayStatRepository = dStRepo;
    }

    /**
     * Compute statistics for all sensors present in database
     * @return false if something goes wrong; true otherwise
     */
    public boolean computeAll() {
        List<Sensor> sensors = mSensorRepository.getAllSensors();
        boolean success = true;

        for (Sensor sensor : sensors) {
            System.out.println("Compute stats for sensor " + sensor.getId());

            try {
                computeStatsForSensor(sensor.getId(), StatUtils.getDateAtMidnight(new Date()));
            } catch (Exception e) {
                System.out.println("Error while computing stats for this sensor...");
                e.printStackTrace();
                success = false;
                continue;
            }

            System.out.println("Computing done.");
        }

        return success;
    }

    /**
     * Cycle through each period of a given sensor to compute its associated stats.
     *
     * @param id sensor id of the sensor
     * @param lastDay today's date at midnight
     * @throws DaoException thrown if something goes wrong while communicating with the db
     */
    private void computeStatsForSensor(String id, Date lastDay) throws DaoException {
        // Find latest DayStat available in db
        // Start by getting all stats sorted
        List<DayStat> dayStats = mDayStatRepository.findAll(new Sort(Sort.Direction.DESC, "day"));

        Date dayDb;

        // If nothing has been found, the first day will be the first value of the sensor
        if (dayStats == null || dayStats.size() == 0) {
            dayDb = mSensorRepository.getSensor(id).getFirstValue();
        } else {
            // else the first day will be the latest day found in db
            DayStat lastDayStat = dayStats.get(0);
            dayDb = lastDayStat.getDay();
        }

        Date firstDay = StatUtils.getDateAtMidnight(dayDb);

        // Compute the stat for each day starting at first day
        int currentDay = StatUtils.date2TimeStamp(firstDay);

        while (currentDay < StatUtils.date2TimeStamp(lastDay)) {
            computeStatForDay(id, currentDay);
            currentDay += 60 * 60 * 24; // 60 seconds * 60 minutes * 24h = number of seconds in a day
        }

        // if the current day in db has already been processed, return
        if (!(firstDay.getTime() < lastDay.getTime()))
            return;

        // Update each period
        updatePeriod(id, firstDay, lastDay);

    }

    private void updatePeriod(String id, Date firstDay, Date lastDay) {
        int currentDay = StatUtils.date2TimeStamp(firstDay);

        while (currentDay < StatUtils.date2TimeStamp(lastDay)) {
            boolean update = true;

            // Find the day to add to each period
            List<DayStat> days = mDayStatRepository.findBySensorIdAndDay(id, StatUtils.getDateAtMidnight(StatUtils.timestamp2Date(currentDay)));

            if (days == null)
                update = false;

            DayStat newDay = null;

            if (update)
                newDay = days.get(0);

            if (newDay == null)
                update = false;

            if (update) {
                // Recompute the stats for each period
                updateOrCreatePeriodStat(id, newDay, Period.DAY);
                updateOrCreatePeriodStat(id, newDay, Period.WEEK);
                updateOrCreatePeriodStat(id, newDay, Period.MONTH);
                updateOrCreatePeriodStat(id, newDay, Period.YEAR);
                updateOrCreatePeriodStat(id, newDay, Period.ALLTIME);
            }

            currentDay += 60 * 60 * 24; // 60 seconds * 60 minutes * 24h = number of seconds in a day
        }

    }

    private void updateOrCreatePeriodStat(String id, DayStat dayStat, Period period) {
        PeriodStat periodStat = mStatRepository.findBySensorIdAndPeriod(id, period).get(0);

        if (periodStat == null)
            periodStat = new PeriodStat(id, period);
        else
            removeExistingStats(id, period); // Remove stats for this sensor

        // Update period stat
        periodStat.removeFirstDay();

        periodStat.addDayInList(dayStat);
        periodStat.recompute();

        mStatRepository.save(periodStat);
    }

    /**
     * Compute stat for a sensor and a given day.
     * @param id the String id of the sensor
     * @param currentDay the current day as an epoch timestamp (in seconds)
     */
    private void computeStatForDay(String id, int currentDay) {
        List<List<Integer>> values;
        try {
            values = mSensorRepository.getValues(id, currentDay, currentDay + 60*60*24);
        } catch (DaoException e) {
            mLogger.debug("No values found for day: " + StatUtils.timestamp2Date(currentDay));
            return;
        }
        Sensor sensor = mSensorRepository.getSensor(id);
        DayStatCreator creator = new DayStatCreator(sensor, currentDay, values);
        DayStat dayStat;
        try {
            dayStat = creator.createStat();
        } catch (Exception e) {
            mLogger.debug(e.toString());
            return;
        }

        if (dayStat == null) {
            mLogger.debug("DayStat null");
            return;
        }

        removeExistingDayStats(id, StatUtils.timestamp2Date(currentDay));
        mDayStatRepository.save(dayStat);
    }

    /**
     * Remove DayStats in database corresponding to a given sensor id and date
     *
     * @param sensorId the sensor id
     * @param d        the date of the day
     */
    private void removeExistingDayStats(String sensorId, Date d) {
        List<DayStat> dayStats = mDayStatRepository.findBySensorIdAndDay(sensorId, d);

        for (DayStat dayStat : dayStats)
            mDayStatRepository.delete(dayStat);
    }

    /**
     * Remove PeriodStat in database corresponding to a given sensor id and period
     *
     * @param sensorId the sensor id
     * @param p       a Period
     */
    private void removeExistingStats(String sensorId, Period p) {
        List<PeriodStat> stats = mStatRepository.findBySensorIdAndPeriod(sensorId, p);

        for (PeriodStat stat : stats)
            mStatRepository.delete(stat);
    }
}
