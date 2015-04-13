package org.starfishrespect.myconsumption.server.entities;

import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

    @Id
    private String id;
    private String name;
    private String password;
    private List<String> sensors;

    private Set<Role> roles = new HashSet<Role>();

    public User() {}

//    public User(String name, String password) {
//        this.name = name;
//        this.password = password;
//        this.sensors = new ArrayList<>();
//    }

    public User(User user) {
        super();
        this.id = user.getId();
        this.name = user.getName();
        this.password = user.getPassword();
        this.sensors = user.getSensors();
        this.roles = user.getRoles();
    }


    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, name='%s', sensors='%s']",
                id, name, this.printSensors());
    }

    private String printSensors() {
        return sensors == null ? "" : sensors.toString();
    }

    // Getters and setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).equals(sensorId)) {
                sensors.remove(i);
                return;
            }
        }
    }
}