package org.starfishrespect.myconsumption.server.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.List;

/**
 * Created by thibaud on 02.04.15.
 * Tool that computes the statistics based on last data. It also saves the statistics in the database.
 */
public class StatisticsUpdater {
    @Autowired
    private SensorRepository mSensorRepository;

    @Autowired
    private StatRepository mStatRepository;

    public StatisticsUpdater(SensorRepository seRepo, StatRepository stRepo) {
        this.mSensorRepository = seRepo;
        this.mStatRepository = stRepo;
    }

    /**
     * Compute statistics for all sensors present in database
     *
     * @return false if something goes wrong; true otherwise
     */
    public boolean computeAll() {
        List<Sensor> sensors = mSensorRepository.getAllSensors();
        boolean success = true;

        for (Sensor sensor : sensors) {
            System.out.println("Compute stats for sensor " + sensor.getId());

            try {
                computeStatsForSensor(sensor.getId());
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
     * @throws DaoException thrown if something goes wrong while communicating with the db
     */
    private void computeStatsForSensor(String id) throws DaoException {
        for (Period p : Period.values()) {
            List<List<Integer>> values = mSensorRepository.getValues(id, StatUtils.getStartTime(p), StatUtils.getEndTime());
            List<List<Integer>> oldValues = mSensorRepository.getValues(id, StatUtils.getStartTime(p, 2), StatUtils.getStartTime(p));
            Sensor sensor = mSensorRepository.getSensor(id);

            StatCreator creator = new StatCreator(mSensorRepository, sensor, p, values, oldValues);
            Stat stat = creator.createStat();

            // Remove corresponding stat from db and insert new one
            removeExistingStats(id, p);
            mStatRepository.save(stat);
        }
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
