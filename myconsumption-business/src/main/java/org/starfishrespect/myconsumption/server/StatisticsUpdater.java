package org.starfishrespect.myconsumption.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.spi.CalendarDataProvider;

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
            List<List<Integer>> lOldValues = mSensorRepository.getValues(sensor, getStartTime(p, 2), getStartTime(p));

            stat.setAverage(computeAverage(lValues));
            stat.setAverageDay(computeAverageDay(sensor, p));
            stat.setAverageNight(computeAverageNight(sensor, p));
            stat.setMinValue(computeMin(lValues));
            stat.setMinTimestamp(computeMinTimestamp(lValues));
            stat.setMaxValue(computeMax(lValues));
            stat.setMaxTimestamp(computeMaxTimestamp(lValues));
            stat.setConsumption(computeConsumption(lValues));
            stat.setDiffLastTwo(computeDiff(lValues, lOldValues));

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


    public Integer computeAverage(List<List<Integer>> lValues) {
        if (lValues == null || lValues.size() == 0)
            return null;

        int sum = 0;

        for(List<Integer> l : lValues)
            sum += l.get(1);

        return sum/lValues.size();
    }

    /**
     * Get today's date but at a given hour.
     * @param hour the hour to set
     * @return today's date calendar at a given hour.
     */
    private Calendar getCalendar(int hour) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // If the hour we've just set is in the future...
        if (date.after(new GregorianCalendar()))
            date.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day

        return date;
    }

    private int getNumberOfLoop(Period p) {
        switch (p) {
            case ALLTIME:
                return Integer.MAX_VALUE;
            case DAY:
                return 1;
            case WEEK:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK);
            case MONTH:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
            case YEAR:
                return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR);
            default:
                return -1;
        }
    }

    private Integer computeAverageNight(String sensor, Period p) throws DaoException {
        Integer averageNight = 0;

        // Find timestamps of last night (date1 = beginning, date2 = end)
        Calendar date2 = getCalendar(7);
        Calendar date1 = (Calendar) date2.clone();
        date1.add(Calendar.HOUR_OF_DAY, -9);

        int iMax = getNumberOfLoop(p);

        for (int i = iMax - 1; i >= 0; i--) {

            List<List<Integer>> lValues = mSensorRepository.getValues(
                    sensor,
                    (int) date1.getTimeInMillis() / 1000,
                    (int) date2.getTimeInMillis() / 1000);

            Integer avg = computeAverage(lValues);

            if (avg != null)
                averageNight += avg;

            date1.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
            date2.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
        }

        return averageNight;
    }

    private Integer computeAverageDay(String sensor, Period p) throws DaoException {
        Integer averageDay = 0;

        // Find timestamps of last day (date1 = beginning, date2 = end)
        Calendar date2 = getCalendar(22);
        Calendar date1 = (Calendar) date2.clone();
        date1.add(Calendar.HOUR_OF_DAY, -15);

        int iMax = getNumberOfLoop(p);

        for (int i = iMax - 1; i >= 0; i--) {

            List<List<Integer>> lValues = mSensorRepository.getValues(
                    sensor,
                    (int) date1.getTimeInMillis() / 1000,
                    (int) date2.getTimeInMillis() / 1000);

            Integer avg = computeAverage(lValues);

            if (avg != null)
                averageDay += avg;

            date1.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
            date2.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
        }

        return averageDay;
    }

    public int computeMaxTimestamp(List<List<Integer>> lValues) {
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

    public int computeMax(List<List<Integer>> lValues) {
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

    public int computeMinTimestamp(List<List<Integer>> lValues) {
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


    public int computeMin(List<List<Integer>> lValues) {
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

    public Integer computeConsumption(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return null;

        int total = 0;

        for (List<Integer> l : lValues)
            total += l.get(1);

        return total;
    }

    private Integer computeDiff(List<List<Integer>> lValues, List<List<Integer>> lOldValues) {
        if (lValues.size() == 0 || lOldValues.size() == 0)
            return null;

        return computeConsumption(lValues) - computeConsumption(lOldValues);
    }
}
