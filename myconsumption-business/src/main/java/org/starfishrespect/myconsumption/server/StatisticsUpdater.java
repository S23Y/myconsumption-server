package org.starfishrespect.myconsumption.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by thibaud on 02.04.15.
 * Tool that recomputes the statistics based on last data. It also saves the statistics in the database.
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
     * Compute all data for all sensors present in database
     *
     * @return false if something goes wrong; true otherwise
     */
    public boolean computeAll() {
        List<Sensor> list = mSensorRepository.getAllSensors();
        return compute(mSensorRepository.getAllSensors());
    }

    /**
     * Retrieves and stores the data for one user
     *
     * @return false if something goes wrong; true otherwise
     */
    public boolean compute(List<Sensor> sensors) {
        boolean success = true;

        for (Sensor sensor : sensors) {
            System.out.println("Compute stats for sensor " + sensor.getId());

            try {
                computeAllStats(sensor.getId());
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

    private void computeAllStats(String sensor) throws DaoException {

        for (Period p : Period.values()) {
            Stat stat = new Stat(sensor, p);

/*            System.out.println("Period " + p.toString() + "\n" +
                    "Begin: " + new java.util.Date((long)getStartTime(p)*1000).toString() + "\n" +
                    "End: " + new java.util.Date((long)getEndTime()*1000).toString() + "\n");*/

            List<List<Integer>> lValues = mSensorRepository.getValues(sensor, getStartTime(p), getEndTime());
            stat.setAverage(getAverage(lValues));
            stat.setMinValue(getMin(lValues));
            stat.setMinTimestamp(getMinTimestamp(lValues));
            stat.setMaxValue(getMax(lValues));
            stat.setMaxTimestamp(getMaxTimestamp(lValues));
            stat.setConsumption(getConsumption(lValues));

            List<List<Integer>> lOldValues = mSensorRepository.getValues(sensor, getStartTime(p, 2), getStartTime(p));
            stat.setDiffLastTwo(getDiff(lValues, lOldValues));

            mStatRepository.save(stat);
        }
    }

    private int getStartTime(Period p) {
        return getStartTime(p, 1);
    }

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param period a Period
     * @param m an int used as a multiplier
     * @return
     */
    private int getStartTime(Period period, int m) {
        int day = 86400; // 86400 = one day in Unix Timestamp

        switch (period) {
            case ALLTIME:
                return 0;
            case DAY:
                return (int) (System.currentTimeMillis() / 1000L) - (day * m);
            case WEEK:
                return (int) (System.currentTimeMillis() / 1000L) - (day * 7 * m);
            case MONTH:
                return (int) (System.currentTimeMillis() / 1000L)
                        - (day * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) * m);
            case YEAR:
                return (int) (System.currentTimeMillis() / 1000L)
                        - (day * Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR) * m);
            default:
                return -1;
        }
    }

    private int getEndTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }


    public Integer getAverage(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int sum = 0;

        for(List<Integer> l : lValues)
            sum += l.get(1);

        return sum/lValues.size();
    }

    public int getMaxTimestamp(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return -1;

        int max = 0;
        int timestamp = 0;

        for (List<Integer> l : lValues) {
            if (l.get(1) > max) {
                timestamp = l.get(0);
                max = l.get(1);
            }
        }

        return timestamp;
    }

    public int getMax(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return -1;

        int max = 0;

        for (List<Integer> l : lValues) {
            if (l.get(1) > max) {
                max = l.get(1);
            }
        }

        return max;
    }

    public int getMinTimestamp(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return -1;

        int min = Integer.MAX_VALUE;
        int timestamp = 0;

        for (List<Integer> l : lValues) {
            if (l.get(1) < min) {
                timestamp = l.get(0);
                min = l.get(1);
            }
        }

        return timestamp;
    }


    public int getMin(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return -1;

        int min = Integer.MAX_VALUE;
        for (List<Integer> l : lValues) {
            if (l.get(1) < min) {
                min = l.get(1);
            }
        }

        return min;
    }

    public Integer getConsumption(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int total = 0;

        for (List<Integer> l : lValues)
            total += l.get(1);

        return total;
    }

    private Integer getDiff(List<List<Integer>> lValues, List<List<Integer>> lOldValues) {
        if (lValues.size() == 0 || lOldValues.size() == 0)
            return null;

        return getConsumption(lValues) - getConsumption(lOldValues);
    }
}
