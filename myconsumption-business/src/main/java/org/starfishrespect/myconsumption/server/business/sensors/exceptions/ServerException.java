package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Used for server errors (5XX)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */

public class ServerException extends RetrieveException {
    int errorCode;


    public ServerException(int errorCode, String message) {
        super(message + " - Error code : " + errorCode);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}