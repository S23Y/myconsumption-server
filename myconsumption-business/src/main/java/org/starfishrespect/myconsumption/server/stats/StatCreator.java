package org.starfishrespect.myconsumption.server.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;

import java.util.Calendar;
import java.util.List;

/**
 * Created by thibaud on 26.04.15.
 * Creates a Stat object based on a sensor a period and its associated values.
 */
public class StatCreator {

    @Autowired
    private SensorRepository mSensorRepository;
    private Sensor sensor;
    private Period period;
    private List<List<Integer>> values;
    private List<List<Integer>> oldValues;

    public StatCreator(SensorRepository seRepo, Sensor sensor, Period period, List<List<Integer>> values, List<List<Integer>> oldValues) {
        this.mSensorRepository = seRepo;
        this.sensor = sensor;
        this.period = period;
        this.values = values;
        this.oldValues = oldValues;
    }

    public Stat createStat() throws DaoException {
        if (sensor == null || period == null)
            return null;

        Stat stat = new Stat(sensor.getId(), period);

        // Compute and set data for stats
        System.out.println("\n\n\n\n" + period.toString() + " sensor: " + sensor.getName());

        stat.setAverage(computeAverage(values));
        stat.setMinValue(computeMin());
        stat.setMinTimestamp(computeMinTimestamp());
        stat.setMaxValue(computeMax());
        stat.setMaxTimestamp(computeMaxTimestamp());

        int consoTot = computeConsumption();
        Integer avgDay = computeAverageDay();
        stat.setConsumption(consoTot);
        stat.setConsumptionDay(avgDay);
        stat.setConsumptionNight(consoTot - avgDay);

        stat.setDiffLastTwo(computeDiff());

        return stat;
    }

    /**
     * Find total consumption over of a given set of values
     * @return total consumption over of a given set of values
     */
    private Integer computeConsumption() {
        if (values.size() == 0)
            return 0;

        double total = 0.0;

        for (int i = 0; i < values.size(); i++) {
            double currentValue = values.get(i).get(1);
            double duration;

            if (i + 1 < values.size())
                duration = values.get(i + 1).get(0) - values.get(i).get(0);
            else
                duration = values.get(i - 1).get(0) - values.get(i).get(0);

            duration =  duration > 0 ? duration : -duration;

            if(duration == 0)
                continue;

            total += currentValue * duration / (60.0 * 60.0);
        }

        return (int) total;
    }

    /**
     * Compute the average of the given values
     * @return the average of the given values
     * @param valueList list of values
     */
    private Integer computeAverage(List<List<Integer>> valueList) {
        if (valueList == null || valueList.size() == 0)
            return 0;

        double total = 0.0;

        for (List<Integer> l : valueList)
            total += l.get(1);

        return (int) (total / valueList.size());
    }

    /**
     * Find timestamp associated to max value of a given set of values
     * @return timestamp associated to max value of a given set of values
     */
    private int computeMaxTimestamp() {
        if (values.size() == 0)
            return -1;

        int max = 0;
        int timestamp = 0;

        for (List<Integer> l : values) {
            if (l.get(1) > max) {
                timestamp = l.get(0);
                max = l.get(1);
            }
        }

        return timestamp;
    }

    /**
     * Find max value of a given set of values
     * @return max value of a given set of values
     */
    private int computeMax() {
        if (values.size() == 0)
            return -1;

        int max = 0;

        for (List<Integer> l : values) {
            if (l.get(1) > max) {
                max = l.get(1);
            }
        }

        return max;
    }

    /**
     * Find timestamp associated to min value of a given set of values
     * @return timestamp associated to min value of a given set of values
     */
    private int computeMinTimestamp() {
        if (values.size() == 0)
            return -1;

        int min = Integer.MAX_VALUE;
        int timestamp = 0;

        for (List<Integer> l : values) {
            if (l.get(1) < min) {
                timestamp = l.get(0);
                min = l.get(1);
            }
        }

        return timestamp;
    }

    /**
     * Find min value of a given set of values
     * @return min value of a given set of values
     */
    private int computeMin() {
        if (values.size() == 0)
            return -1;

        int min = Integer.MAX_VALUE;
        for (List<Integer> l : values) {
            if (l.get(1) < min) {
                min = l.get(1);
            }
        }

        return min;
    }

    /**
     * Compute the consumption over day for the period p only during the day.
     *
     * @return the consumption over day for the period p only during the day.
     * @throws org.starfishrespect.myconsumption.server.exceptions.DaoException if something goes wrong while communicating with the db
     */
    private Integer computeAverageDay() throws DaoException {
        // Find timestamps of last day (date1 = beginning, date2 = end)
        Calendar date2 = StatUtils.getCalendar(22);
        Calendar date1 = (Calendar) date2.clone();
        date1.add(Calendar.HOUR_OF_DAY, -15);

        return averageOverDays(date1, date2);
    }

    /**
     * Compute the consumption over the given period p only during the day.
     * @param date1 starting day 1
     * @param date2 starting day 2
     * @return the consumption over the given period p only during the day.
     * @throws DaoException if something goes wrong while communicating with the db
     */
    private Integer averageOverDays(Calendar date1, Calendar date2) throws DaoException {
        Integer consumptionOverPeriod = 0;

        Calendar firstValue = Calendar.getInstance();
        firstValue.setTime(sensor.getFirstValue());

        int iMax = StatUtils.getNumberOfDaysInPeriod(period);
        int counter = 0;

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

            List<List<Integer>> subValues = mSensorRepository.getValues(
                    sensor.getId(),
                    (int) (date1.getTimeInMillis() / 1000),
                    (int) (date2.getTimeInMillis() / 1000));

            Integer consumptionOverSubPeriod = computeAverage(subValues);

            if (consumptionOverSubPeriod != null) {
                consumptionOverPeriod += consumptionOverSubPeriod;
                counter++;
            }

            date1.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
            date2.add(Calendar.DAY_OF_MONTH, -1); // ...decrement by one day
        }

        return consumptionOverPeriod / counter;
    }

    /**
     * Diff between two total consumptions over of two sets of values
     * @return diff between two total consumptions over of two sets of values
     */
    private Integer computeDiff() {
        if (values.size() == 0 || oldValues.size() == 0)
            return 0;

        return computeAverage(values) - computeAverage(oldValues);
    }
}
