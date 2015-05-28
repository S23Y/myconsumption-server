package org.starfishrespect.myconsumption.server.business.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Represent statistics info about a day.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class DayStat {
    @Id
    private String id;
    @Indexed
    private String sensorId;
    @Indexed
    private Date day;
    private Integer maxTimestamp;
    private Integer maxValue;
    private Integer minTimestamp;
    private Integer minValue;
    private Integer average;
    private Integer consumptionDay;
    private Integer consumptionNight;

    public DayStat() {
    }

    public DayStat(String sensorId, Date day) {
        this.sensorId = sensorId;
        this.day = day;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Date getDay() {
        return day;
    }

    public Integer getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(Integer maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMinTimestamp() {
        return minTimestamp;
    }

    public void setMinTimestamp(Integer minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getAverage() {
        return average;
    }

    public void setAverage(Integer average) {
        this.average = average;
    }

    public Integer getConsumption() {
        return consumptionDay + consumptionNight;
    }

    public Integer getConsumptionDay() {
        return consumptionDay;
    }

    public void setConsumptionDay(Integer consumptionDay) {
        this.consumptionDay = consumptionDay;
    }

    public Integer getConsumptionNight() {
        return consumptionNight;
    }

    public void setConsumptionNight(Integer consumptionNight) {
        this.consumptionNight = consumptionNight;
    }
}
