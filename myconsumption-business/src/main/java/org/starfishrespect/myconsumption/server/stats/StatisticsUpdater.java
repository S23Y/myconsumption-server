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

import java.util.Calendar;
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
                computeStatsForSensor(sensor.getId(), StatUtils.getDateAtMidnight());
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

        Date firstDay;

        // If nothing has been found, the first day will be the first value of the sensor
        if (dayStats == null || dayStats.size() == 0) {
            firstDay = mSensorRepository.getSensor(id).getFirstValue();
        } else {
            // else the first day will be the latest day found in db
            DayStat lastDayStat = dayStats.get(dayStats.size() - 1);
            firstDay = lastDayStat.getDay();
        }

        // Compute the stat for each day starting at first day
        int currentDay = StatUtils.date2TimeStamp(firstDay);

        while (currentDay <= StatUtils.date2TimeStamp(lastDay)) {
            computeStatForDay(id, currentDay);
            currentDay += 60 * 60 * 24; // 60 seconds * 60 minutes * 24h = number of seconds in a day
        }


        // Compute the stats for each period
        // DAY
        // Remove stats for this sensor
        removeExistingStats(id, Period.DAY);
        // Create and save the period stat
        mStatRepository.save(createPeriodStat(id, lastDay, lastDay, Period.DAY));

        // WEEK
        // get start of this week
        // TODO ne fonctonne pas. exemple si on est mardi, il va renvoyer lundi . Or on voudra le lundi d'avant...
        Calendar cal = StatUtils.date2Calendar(lastDay);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // get start of this week
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        // Remove stats for this sensor
        removeExistingStats(id, Period.WEEK);
        // Create and save the period stat
        mStatRepository.save(createPeriodStat(id, cal.getTime(), lastDay, Period.WEEK));

        // MONTH
        // todo idem
        cal.set(Calendar.DAY_OF_MONTH, StatUtils.date2Calendar(lastDay).getActualMinimum(Calendar.DAY_OF_MONTH));
        // Remove stats for this sensor
        removeExistingStats(id, Period.MONTH);
        // Create and save the period stat
        mStatRepository.save(createPeriodStat(id, cal.getTime(), lastDay, Period.MONTH));

        // YEAR
        // Remove stats for this sensor
        removeExistingStats(id, Period.YEAR);
        mStatRepository.save(year);
        // ALLTIME
        // Remove stats for this sensor
        removeExistingStats(id, Period.ALLTIME);
        mStatRepository.save(alltime);


    }

    private PeriodStat createPeriodStat(String sensorId, Date firstDay, Date lastDay, Period period) {
        PeriodStat periodStat = new PeriodStat(sensorId, period);
        int currentDay = StatUtils.date2TimeStamp(firstDay);
        while (currentDay <= StatUtils.date2TimeStamp(lastDay)) {
            periodStat.addDayInList(mDayStatRepository.findBySensorIdAndDay(sensorId, StatUtils.timestamp2Date(currentDay)).get(0));
            currentDay += 60 * 60 * 24; // 60 seconds * 60 minutes * 24h = number of seconds in a day
        }
        periodStat.recompute();

        return periodStat;
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
