package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Created by pat on 5/03/14.
 * Used for request errors (4XX)
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
