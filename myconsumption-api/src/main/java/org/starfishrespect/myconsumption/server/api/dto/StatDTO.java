package org.starfishrespect.myconsumption.server.api.dto;

import java.io.Serializable;

public class StatDTO implements Serializable {

    private String sensorId;
    private Period period;
    private Integer maxTimestamp;
    private Integer maxValue;
    private Integer minTimestamp;
    private Integer minValue;
    private Integer averageConsumption;
//    private Integer consumptionDay;
//    private Integer consumptionNight;
//    private Integer consumption;
    private Integer diffLastTwo;


    public StatDTO() {
    }

    public StatDTO(String s, Period p) {
        sensorId = s;
        period = p;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Period getPeriod() {
        return period;
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

    public Integer getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(Integer averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

//    public Integer getConsumptionDay() {
//        return consumptionDay;
//    }
//
//    public void setConsumptionDay(Integer consumptionDay) {
//        this.consumptionDay = consumptionDay;
//    }
//
//    public Integer getConsumptionNight() {
//        return consumptionNight;
//    }
//
//    public void setConsumptionNight(Integer consumptionNight) {
//        this.consumptionNight = consumptionNight;
//    }
//
//    public Integer getConsumption() {
//        return consumption;
//    }
//
//    public void setConsumption(Integer consumption) {
//        this.consumption = consumption;
//    }

    public Integer getDiffLastTwo() {
        return diffLastTwo;
    }

    public void setDiffLastTwo(Integer diffLastTwo) {
        this.diffLastTwo = diffLastTwo;
    }
}