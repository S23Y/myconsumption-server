package org.starfishrespect.myconsumption.server.entities;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {

    @Id
    private String id;

    private String name;
    private String password;
    private List<String> sensors;
    private String registerId;

    public User() {}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        sensors = new ArrayList<String>();
    }


    @Override
    public String toString() {
        return String.format(
                "User [id=%s, name='%s', sensors='%s']",
                id, name, this.printSensors());
    }

    private String printSensors() {
        return sensors == null ? "" : sensors.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(String sensorID) {
        sensors.add(sensorID);
    }

    public void removeSensor(String sensorId) {
        for (int i = 0; i < sensors.size(); i ++) {
            if (sensors.get(i).equals(sensorId)) {
                sensors.remove(i);
                return;
            }
        }
    }
}