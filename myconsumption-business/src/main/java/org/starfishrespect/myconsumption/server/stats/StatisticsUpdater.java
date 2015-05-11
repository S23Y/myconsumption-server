package org.starfishrespect.myconsumption.server.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.DayStat;
import org.starfishrespect.myconsumption.server.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.DayStatRepository;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.PeriodStatRepository;

import java.util.Calendar;
import java.util.Collections;
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
    private PeriodStatRepository mPeriodStatRepository;
    @Autowired
    private DayStatRepository mDayStatRepository;

    private final Logger mLogger = LoggerFactory.getLogger(StatisticsUpdater.class);

    public StatisticsUpdater(SensorRepository seRepo, PeriodStatRepository stRepo, DayStatRepository dStRepo) {
        this.mSensorRepository = seRepo;
        this.mPeriodStatRepository = stRepo;
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
     * @param today today's date at midnight
     * @throws DaoException thrown if something goes wrong while communicating with the db
     */
    private void computeStatsForSensor(String id, Date today) throws DaoException {
        // Find latest DayStat available in db
        // Start by getting all stats sorted for this sensor id
        List<DayStat> dayStats = mDayStatRepository.findBySensorId(id);
        Collections.sort(dayStats, new DayStatComparator());

        Date dayDb;

        // If nothing has been found, the first day will be the first value of the sensor
        if (dayStats.size() == 0) {
            dayDb = mSensorRepository.getSensor(id).getFirstValue();
        } else {
            // else the first day will be the latest day found in db
            DayStat lastDayStat = dayStats.get(dayStats.size() - 1);
            dayDb = lastDayStat.getDay();
        }

        Calendar firstDay = StatUtils.date2Calendar(StatUtils.getDateAtMidnight(dayDb));
        Calendar last = StatUtils.date2Calendar(today);
        // Compute the stat for each day starting at first day
        Calendar currentDay = StatUtils.date2Calendar(firstDay.getTime());

        while (last.compareTo(currentDay) > 0) {
            computeStatForDay(id, StatUtils.calendar2TimeStamp(currentDay));
            currentDay.add(Calendar.DATE, 1);
        }

        // if the current day in db has already been processed, return
        if (!(firstDay.getTimeInMillis() < today.getTime()))
            return;

        // Update each period
        updatePeriod(id, firstDay.getTime());

    }

    /**
     * Update the stat for all periods of a given sensor from a specific date.
     * @param id id of the sensor to update
     * @param firstDay specific Date at which we start updating stats
     */
    private void updatePeriod(String id, Date firstDay) {
        List<DayStat> dayStats = mDayStatRepository.findBySensorId(id);
        Collections.sort(dayStats, new DayStatComparator());

        for(DayStat newDay : dayStats) {
            if (newDay.getDay().getTime() <= firstDay.getTime())
                continue;

            updateOrCreatePeriodStat(id, newDay, Period.DAY);
            updateOrCreatePeriodStat(id, newDay, Period.WEEK);
            updateOrCreatePeriodStat(id, newDay, Period.MONTH);
            updateOrCreatePeriodStat(id, newDay, Period.YEAR);
            updateOrCreatePeriodStat(id, newDay, Period.ALLTIME);
        }
    }

    /**
     * Update or create stats for a sensor at a given period by adding a DayStat.
     * @param id id of the sensor to update
     * @param dayStat DayStat to add to this period
     * @param period given Period
     */
    private void updateOrCreatePeriodStat(String id, DayStat dayStat, Period period) {
        List<PeriodStat> periodStats = mPeriodStatRepository.findBySensorIdAndPeriod(id, period);
        PeriodStat periodStat = null;

        if (periodStats == null || periodStats.size() == 0)
            periodStat = new PeriodStat(id, period);
        else
            periodStat = periodStats.get(0);

        // Update period stat
        periodStat.removeFirstDay();

        periodStat.addDayInList(dayStat);
        periodStat.recompute();

        mPeriodStatRepository.save(periodStat);
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

        //removeExistingDayStats(id, StatUtils.timestamp2Date(currentDay));
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
        List<PeriodStat> stats = mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, p);

        for (PeriodStat stat : stats)
            mPeriodStatRepository.delete(stat);
    }
}
