package org.starfishrespect.myconsumption.server.api.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Simple REST response that can be used for any REST call, when you need
 * a numeric response code, and which may contain any Object as a payload
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
@JsonAutoDetect
public class SimpleResponseDTO {

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = -1;
    public static final int STATUS_ALREADY_EXISTS = -2;
    public static final int STATUS_INVALID_SETTINGS = -3;
    public static final int STATUS_SENSOR_INSERTED_BUT_NOT_LINKED = -4;

    @JsonProperty
    private int status;
    @JsonProperty
    private Object response;


    public SimpleResponseDTO() {
    }

    public SimpleResponseDTO(boolean success, Object value) {
        this.response = value;
        if (success) {
            status = STATUS_SUCCESS;
        } else {
            status = STATUS_FAILURE;
        }
    }

    public SimpleResponseDTO(int status, Object response) {
        this.status = status;
        this.response = response;
    }

    public int getStatus() {
        return status;
    }

    public Object getResponse() {
        return response;
    }
}
