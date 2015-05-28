package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Used for request errors (4XX)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class RequestException extends RetrieveException {
    int errorCode;

    public RequestException(int errorCode, String message) {
        super(message + " - Error code : " + errorCode);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
