package org.starfishrespect.myconsumption.server.business.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.business.stats.StatUtils;

import java.util.ArrayList;
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

    /**
     * Represent the days stat of this period
     */
    private List<DayStat> daysInPeriod;
    /**
     * Represent the days stat of the period previous to the one represented by this object.
     * It is needed to compute the diff between the consumption of the period represented by
     * this object and the consumption of the previous period.
     */
    private List<DayStat> daysInPreviousPeriod;

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
        daysInPeriod = new ArrayList<>();
        daysInPreviousPeriod = new ArrayList<>();
    }

    public boolean addDayInList(DayStat day) {
        return daysInPeriod.add(day);
    }

    public void removeFirstDay() {
        if (daysInPeriod.size() == 0)
            return;
        if (this.daysInPeriod.size() < StatUtils.getNumberOfDaysInPeriod(period))
            return;

        DayStat removed = daysInPeriod.remove(0);

        // add this to the older period list
        daysInPreviousPeriod.add(removed);

        if (this.daysInPreviousPeriod.size() <= StatUtils.getNumberOfDaysInPeriod(period))
            return;

        daysInPreviousPeriod.remove(0);
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

        average = average / daysInPeriod.size();

        int oldConso = 0;

        for (DayStat oldDay : daysInPreviousPeriod) {
            oldConso += oldDay.getConsumption();
        }

        diffLastTwo = this.getConsumption() - oldConso;
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

    public List<DayStat> getDaysInPreviousPeriod() {
        return daysInPreviousPeriod;
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
