package org.starfishrespect.myconsumption.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.starfishrespect.myconsumption.server.business.sensors.SensorSettings;

import java.util.Date;

/**
 * Adapted from Patrick Herbeuval by Thibaud Ledent on 11.03.15.
 * Class that represent a sensor in database (with all the information needed
 * to retrieve data)
 */
public class Sensor {
    @Id
    private String id;
    private String name;
    @Indexed
    private String type;
    private Date firstValue = new Date(0);
    private Date lastValue = new Date(0);
    private boolean dead = false;
    protected int usageCount = 0;
    protected SensorSettings sensorSettings;

    public Sensor() { }


    public Sensor(String name) {
        this.name = name;
    }

    protected Sensor(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public SensorSettings getSensorSettings() {
        return sensorSettings;
    }

    public void setSensorSettings(SensorSettings sensorSettings) {
        this.sensorSettings = sensorSettings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(Date firstValue) {
        this.firstValue = firstValue;
    }

    public Date getLastValue() {
        return lastValue;
    }

    public void setLastValue(Date lastValue) {
        this.lastValue = lastValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", usageCount=" + usageCount +
                ", sensorSettings=" + sensorSettings +
                ", firstValue=" + firstValue +
                ", lastValue=" + lastValue +
                ", dead=" + dead +
                '}';
    }
}