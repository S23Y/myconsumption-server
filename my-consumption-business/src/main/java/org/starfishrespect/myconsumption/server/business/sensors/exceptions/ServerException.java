package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Created by pat on 5/03/14.
 * Used for server errors (5XX)
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
