package org.starfishrespect.myconsumption.server.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.starfishrespect.myconsumption.server.api.dto.Period;

import java.util.List;

/**
 * Created by thibaud on 03.05.15.
 */
public class PeriodStat {
    @Id
    private String id;
    @Indexed
    private String sensorId;
    @Indexed
    private Period period;

    private List<DayStat> daysInPeriod;

    private Integer maxTimestamp;
    private Integer maxValue;
    private Integer minTimestamp;
    private Integer minValue;
    private Integer average;
    private Integer consumptionDay;
    private Integer consumptionNight;
    private Integer diffLastTwo;

    public PeriodStat(String sensorId, Period period) {
        this.sensorId = sensorId;
        this.period = period;
    }

    public boolean addDayInList(DayStat day) {
        // if last day in list is comes after the day we want to insert
        if (daysInPeriod.get(daysInPeriod.size() - 1).getDay().getTime() > day.getDay().getTime())
            return false;
        else
            return daysInPeriod.add(day);
    }

    public void removeFirstDay() {
        daysInPeriod.remove(0);
    }

    public void recompute() {
        reset();

        for (DayStat day : daysInPeriod) {
            consumptionDay += day.getConsumptionDay();
            consumptionNight += day.getConsumptionNight();
            average += day.getAverage();

            if (day.getMaxValue() > maxValue) {
                maxValue = day.getMaxValue();
                maxTimestamp = day.getMaxTimestamp();
            }

            if (day.getMinValue() < minValue) {
                minValue = day.getMinValue();
                minTimestamp = day.getMinTimestamp();
            }
        }

        average = average / getNumberOfDaysInPeriod();
    }

    private void reset() {
        maxTimestamp = 0;
        maxValue = 0;
        minTimestamp = 0;
        minValue = Integer.MAX_VALUE;
        average = 0;
        consumptionDay = 0;
        consumptionNight = 0;
        diffLastTwo = 0;
    }

    public int getNumberOfDaysInPeriod() {
       return daysInPeriod.size();
    }

    public int getConsumption() {
        return consumptionDay + consumptionNight;
    }

    public String getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Period getPeriod() {
        return period;
    }

    public List<DayStat> getDaysInPeriod() {
        return daysInPeriod;
    }

    public Integer getMaxTimestamp() {
        return maxTimestamp;
    }

    public Integer getMinTimestamp() {
        return minTimestamp;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getAverage() {
        return average;
    }

    public Integer getConsumptionDay() {
        return consumptionDay;
    }

    public Integer getConsumptionNight() {
        return consumptionNight;
    }

    public Integer getDiffLastTwo() {
        return diffLastTwo;
    }
}
