package org.starfishrespect.myconsumption.server.entities;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by thibaud on 11.03.15.
 */
public class Sensor {
    @Id
    private String id;
    private String name;
    private String type;
    private Date firstValue = new Date(0);
    private Date lastValue = new Date(0);
    private boolean dead = false;

    public Sensor() { }

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

}
