package org.starfishrespect.myconsumption.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.Calendar;
import java.util.GregorianCalendar;
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

    /**
     * Cycle through each period of a given sensor to compute its associated stats.
     * @param sensor sensor id of the sensor
     * @throws DaoException thrown if something goes wrong while communicating with the db
     */
    private void computeAllStats(String sensor) throws DaoException {

        for (Period p : Period.values()) {
            Stat stat = new Stat(sensor, p);

/*            System.out.println("Period " + p.toString() + "\n" +
                    "Begin: " + new java.util.Date((long)getStartTime(p)*1000).toString() + "\n" +
                    "End: " + new java.util.Date((long)getEndTime()*1000).toString() + "\n");*/

            List<List<Integer>> lValues = mSensorRepository.getValues(sensor, getStartTime(p), getEndTime());
            List<List<Integer>> lOldValues = mSensorRepository.getValues(sensor, getStartTime(p, 2), getStartTime(p));

            // Compute and set data for stats
            stat.setAverage(computeAverage(lValues));
            stat.setMinValue(computeMin(lValues));
            stat.setMinTimestamp(computeMinTimestamp(lValues));
            stat.setMaxValue(computeMax(lValues));
            stat.setMaxTimestamp(computeMaxTimestamp(lValues));

            Integer consoTot = computeConsumption(lValues);
            Integer consoDay = computeConsumptionDay(sensor, p);
            stat.setConsumption(consoTot);
            stat.setConsumptionDay(consoDay);
            stat.setConsumptionNight(consoTot - consoDay);

            stat.setDiffLastTwo(computeDiff(lValues, lOldValues));

            // Remove corresponding stat from db and insert new one
            removeExistingStats(sensor, p);
            mStatRepository.save(stat);
        }
    }

    /**
     * Remove stats in database corresponding to a given sensor id and period
     * @param sensorId the sensor id
     * @param p the period
     */
    private void removeExistingStats(String sensorId, Period p) {
        List<Stat> stats =  mStatRepository.findBySensorIdAndPeriod(sensorId, p);

        for (Stat stat : stats)
            mStatRepository.delete(stat);
    }

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param p a Period
     * @return the unix timestamp at which we should begin the value retrieval
     */
    private int getStartTime(Period p) {
        return getStartTime(p, 1);
    }

    /**
     * Get the unix timestamp at which we should begin the value retrieval
     * @param period a Period
     * @param m an int used as a multiplier
     * @return the unix timestamp at which we should begin the value retrieval
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

    /**
     * Get the unix timestamp at which we should end the value retrieval
     * @return current system UNIX timestamp
     */
    private int getEndTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }


    /**
     * Compute the average of the given values
     * @param lValues the values from the sensor
     * @return the average of the given values
     */
    public Integer computeAverage(List<List<Integer>> lValues) {
        if (lValues == null || lValues.size() == 0)
            return 0;

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
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar date = new GregorianCalendar(); // get current time
        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22

        // If the hour we've just set is in the future...
        if (date.after(new GregorianCalendar()))
            date.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day

        //System.out.println(dateFormat.format(date.getTime())); //2014/08/06 16:00:22

        return date;
    }

    /**
     * Number of loop needed for computeConsumptionDay function
     * @param p a Period
     * @return the number of loops
     */
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

    /**
     * Compute the consumption over day for the period p only during the day.
     *
     * @param sensorId the sensor id
     * @param p a Period
     * @return the consumption over day for the period p only during the day.
     * @throws DaoException if something goes wrong while communicating with the db
     */
    private Integer computeConsumptionDay(String sensorId, Period p) throws DaoException {
        // Find timestamps of last day (date1 = beginning, date2 = end)
        Calendar date2 = getCalendar(22);
        Calendar date1 = (Calendar) date2.clone();
        date1.add(Calendar.HOUR_OF_DAY, -15);

        return consumptionOverDays(sensorId, p, date1, date2);
    }

    /**
     *Compute the consumption over the given period p only during the day.
     * @param sensorId the sensor id
     * @param p a Period
     * @param date1 starting day 1
     * @param date2 starting day 2
     * @return the consumption over the given period p only during the day.
     * @throws DaoException if something goes wrong while communicating with the db
     */
    private Integer consumptionOverDays(String sensorId, Period p, Calendar date1, Calendar date2) throws DaoException {
        Integer consumptionOverPeriod = 0;

        Sensor sensor = mSensorRepository.getSensor(sensorId);

        Calendar firstValue = Calendar.getInstance();
        firstValue.setTime(sensor.getFirstValue());

        int iMax = getNumberOfLoop(p);

        // Warning: loop in reverse order. Cycle through dates from now to the first value of the sensor added.
        for (int i = iMax - 1; i >= 0; i--) {

            // Take week-end into account. If date1 is 7:00 AM on Sunday...
            if (date1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                // ... skip the weekend
                date1.add(Calendar.DAY_OF_MONTH, -2); // decrement
                date2.add(Calendar.DAY_OF_MONTH, -2); // decrement
            }

            if (date1.before(firstValue))
                break;

//            // Print the dates and associated timestamps
//            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            System.out.println(dateFormat.format(date1.getTime())); //2014/08/06 16:00:22
//            System.out.println(dateFormat.format(date2.getTime())); //2014/08/06 16:00:22
//
//            System.out.println((int) (date1.getTimeInMillis() / 1000));
//            System.out.println((int) (date2.getTimeInMillis() / 1000));

            List<List<Integer>> lValues = mSensorRepository.getValues(
                    sensorId,
                    (int) (date1.getTimeInMillis() / 1000),
                    (int) (date2.getTimeInMillis() / 1000));

            Integer consumptionOverSubPeriod = computeConsumption(lValues);

            if (consumptionOverSubPeriod != null)
                consumptionOverPeriod += consumptionOverSubPeriod;

            date1.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
            date2.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
        }

        return consumptionOverPeriod;
    }

    /**
     * Find timestamp associated to max value of a given set of values
     * @param lValues set of values from the sensor
     * @return timestamp associated to max value of a given set of values
     */
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

    /**
     * Find max value of a given set of values
     * @param lValues set of values from the sensor
     * @return max value of a given set of values
     */
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

    /**
     * Find timestamp associated to min value of a given set of values
     * @param lValues set of values from the sensor
     * @return timestamp associated to min value of a given set of values
     */
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

    /**
     * Find min value of a given set of values
     * @param lValues set of values from the sensor
     * @return min value of a given set of values
     */
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

    /**
     * Find total consumption over of a given set of values
     * @param lValues set of values from the sensor
     * @return total consumption over of a given set of values
     */
    public Integer computeConsumption(List<List<Integer>> lValues) {
        if (lValues.size() == 0)
            return 0;

        int total = 0;

        for (List<Integer> l : lValues)
            total += l.get(1);

        return total;
    }

    /**
     * Diff between two total consumptions over of two sets of values
     * @param lValues set of values from the sensor
     * @return diff between two total consumptions over of two sets of values
     */
    private Integer computeDiff(List<List<Integer>> lValues, List<List<Integer>> lOldValues) {
        if (lValues.size() == 0 || lOldValues.size() == 0)
            return 0;

        return computeConsumption(lValues) - computeConsumption(lOldValues);
    }
}
