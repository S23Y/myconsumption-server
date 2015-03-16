package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Created by pat on 20/02/14.
 * Base Exception Class for all data retrieve exceptions
 */

public class RetrieveException extends Exception {
    public RetrieveException(String message) {
        super(message);
    }
}
