package org.starfishrespect.myconsumption.server.business.sensors.flukso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Simple storage class used to get informations about a sensor on the
 * Flukso API
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FluksoParams {
    String type;
    @JsonProperty("function")
    String name;
    @JsonProperty("class")
    String sensorClass;
    int voltage, current, phase, enabled;
    @JsonProperty("lastupdate")
    ArrayList<Integer> lastUpdate;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSensorClass() {
        return sensorClass;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getCurrent() {
        return current;
    }

    public int getPhase() {
        return phase;
    }

    public int getEnabled() {
        return enabled;
    }

    public ArrayList<Integer> getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "FluksoParams{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", sensorClass='" + sensorClass + '\'' +
                ", voltage=" + voltage +
                ", current=" + current +
                ", phase=" + phase +
                ", enabled=" + enabled +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}