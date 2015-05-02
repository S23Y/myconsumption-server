package org.starfishrespect.myconsumption.server.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.entities.DayStat;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 01.05.15.
 */
public class DayStatCreator {
    private final Sensor sensor;
    private final List<List<Integer>> values;
    private final int currentDay;

    public DayStatCreator(Sensor sensor, int currentDay, List<List<Integer>> values) {
        this.sensor = sensor;
        this.values = values;
        this.currentDay = currentDay;
    }

    public DayStat createStat() throws Exception {
        if (sensor == null || values == null)
            throw new Exception("Cannot create stat for day " + new Date(currentDay));

        DayStat dayStat = new DayStat(sensor.getId(), StatUtils.timestamp2Date(currentDay));

        // Compute and set data for stats
        System.out.println("\n\n" + new Date(currentDay) + " sensor: " + sensor.getName());

        int consoTot = computeConsumption();
        dayStat.setConsumption(consoTot);
//        int consoDay = computeConsumptionDay();
//        dayStat.setConsumptionDay(consoDay);
//        dayStat.setConsumptionNight(consoTot - consoDay);
//        dayStat.setAverage(computeAverage(values));
//        dayStat.setMinValue(computeMin());
//        dayStat.setMinTimestamp(computeMinTimestamp());
//        dayStat.setMaxValue(computeMax());
//        dayStat.setMaxTimestamp(computeMaxTimestamp());

        // todo somewhere else dayStat.setDiffLastTwo(computeDiff());

        return dayStat;
    }

    /**
     * Find the total consumption over the values of a day (in Wh / day)
     *
     * @return if all values are present with 60 seconds intervals, returns the total consumption over
     * the values of a day (in Wh). Otherwise, returns an exception.
     * @throws Exception
     */
    private int computeConsumption() throws Exception {
        if (values == null || values.size() <= 1)
            throw new Exception("Not enough values for this day");

        int oldTimestamp = values.get(0).get(0);
        int total = values.get(0).get(1);

        for (int i = 1; i < values.size(); i++) {
            List<Integer> pair = values.get(i);
            int value = pair.get(1);
            int timestamp = pair.get(0);

            if (timestamp - oldTimestamp != 60)
                throw new Exception("Missing data for this day");

            total += value;

            oldTimestamp = timestamp;
        }

        // Now, we need to convert this in Wh per day
        // Each value we have summed corresponds to Watts taken every 60 seconds.
        // Each value is the RATE of using or producing electrical energy (or how much is being used right now).
        // But summing these values does not mean a lot. We need an energy (Wh) which is the TOTAL amount of
        // electricity used or produced over a period of time.
        // For a day, it means we have to divide by 60 (numbers of values taken in one hour)
        return total / 60;
    }

    /**
     * Compute the average of the given values (in Watt)
     * @return the average of the given values (in Watt)
     */
    private Integer computeAverage() {
        if (values == null || values.size() == 0)
            return 0;

        int total = 0;

        for (List<Integer> l : values)
            total += l.get(1);

        return total / values.size();
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
     * Find max value of a given set of values (in watt)
     * @return max value of a given set of values (in watt)
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
     * Find min value of a given set of values (in watt)
     * @return min value of a given set of values (in watt)
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
}
