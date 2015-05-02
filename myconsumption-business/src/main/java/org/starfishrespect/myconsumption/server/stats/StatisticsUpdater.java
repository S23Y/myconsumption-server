package org.starfishrespect.myconsumption.server.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.DayStat;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
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

        Date firstDay = null;

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

        while (currentDay < StatUtils.date2TimeStamp(lastDay)) {
            computeStatForDay(id, currentDay);
            currentDay += 60 * 60 * 24; // 60 seconds * 60 minutes * 24h = number of seconds in a day
        }


//        for (Period p : Period.values()) {
//            List<List<Integer>> values = mSensorRepository.getValues(id, StatUtils.getStartTime(p), StatUtils.date2TimeStamp(lastDay)));
//            List<List<Integer>> oldValues = mSensorRepository.getValues(id, StatUtils.getStartTime(p, 2), StatUtils.getStartTime(p));
//            Sensor sensor = mSensorRepository.getSensor(id);
//
//            StatCreator creator = new StatCreator(mSensorRepository, sensor, p, values, oldValues);
//            Stat stat = creator.createStat();
//
//            // Remove corresponding stat from db and insert new one
//            removeExistingStats(id, p);
//            mStatRepository.save(stat);
//        }
    }

    private void computeStatForDay(String id, int currentDay) {
        List<List<Integer>> values = null;
        try {
            values = mSensorRepository.getValues(id, currentDay, currentDay + 60*60*24);
        } catch (DaoException e) {
            mLogger.debug("No values found for day: " + StatUtils.timestamp2Date(currentDay));
        }
        Sensor sensor = mSensorRepository.getSensor(id);
        DayStatCreator creator = new DayStatCreator(sensor, currentDay, values);
        DayStat dayStat = null;
        try {
            dayStat = creator.createStat();
        } catch (Exception e) {
            mLogger.debug(e.toString());
        }

        if (dayStat != null)
            System.out.println(dayStat.getDay() + "  consumption: " + dayStat.getConsumption());
        // todo arrivé ici

    }

    /**
     * Remove stats in database corresponding to a given sensor id and period
     *
     * @param sensorId the sensor id
     * @param p        the period
     */
    private void removeExistingStats(String sensorId, Period p) {
        List<Stat> stats = mStatRepository.findBySensorIdAndPeriod(sensorId, p);

        for (Stat stat : stats)
            mStatRepository.delete(stat);
    }
}
