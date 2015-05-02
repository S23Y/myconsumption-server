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

        DayStat dayStat = new DayStat(sensor.getId(), new Date(currentDay));

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

    private int computeConsumption() throws Exception {
        if (values == null || values.size() == 0)
            throw new Exception("No values for this day");

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

        return total;
    }
}
