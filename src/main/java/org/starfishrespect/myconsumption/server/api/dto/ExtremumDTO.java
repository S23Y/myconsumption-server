package org.starfishrespect.myconsumption.server.api.dto;

/**
 * Created by thibaud on 20.02.15.
 */
public class ExtremumDTO {
    private Integer timestamp;
    private Integer value;

    public ExtremumDTO() {
    }

    public ExtremumDTO(Integer timestamp, Integer value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public Integer getValue() {
        return value;
    }
}
