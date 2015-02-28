package biz.manex.sr.myconsumption.business.sensors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Class that represent a sensor in database (with all the information needed
 * to retrieve data)
 */
public abstract class Sensor {
    @Id
    protected String id;
    protected String name;
    @Indexed
    protected String type;
    protected int usageCount = 0;
    protected SensorSettings sensorSettings;
    protected Date firstValue = new Date(0);
    protected Date lastValue = new Date(0);
    protected boolean dead = false;

    public Sensor(String name) {
        this.name = name;
    }

    protected Sensor(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Sensor() {
    }

    public SensorSettings getSensorSettings() {
        return sensorSettings;
    }

    public void setSensorSettings(SensorSettings sensorSettings) {
        this.sensorSettings = sensorSettings;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
