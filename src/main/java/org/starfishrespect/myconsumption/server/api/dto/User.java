package org.starfishrespect.myconsumption.server.api.dto;

import org.springframework.data.annotation.Id;
import java.util.List;

public class User {

    @Id
    private String id;

    private String name;
    private List<String> sensors;

    public User() {}

    public User(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, name='%s', sensors='%s']",
                id, name, sensors.toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }
}