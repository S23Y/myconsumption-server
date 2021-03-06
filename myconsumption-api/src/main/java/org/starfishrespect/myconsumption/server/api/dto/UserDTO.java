package org.starfishrespect.myconsumption.server.api.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

/**
 * REST representation for a user
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
@JsonAutoDetect
public class UserDTO {
    private String name;
    private List<String> sensors;

    public UserDTO() {
    }

    public UserDTO(String name) {
        this.name = name;
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
