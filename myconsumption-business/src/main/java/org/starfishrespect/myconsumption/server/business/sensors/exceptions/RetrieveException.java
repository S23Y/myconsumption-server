package org.starfishrespect.myconsumption.server.business.sensors.exceptions;

/**
 * Base Exception Class for all data retrieve exceptions
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */

public class RetrieveException extends Exception {
    public RetrieveException(String message) {
        super(message);
    }
}
