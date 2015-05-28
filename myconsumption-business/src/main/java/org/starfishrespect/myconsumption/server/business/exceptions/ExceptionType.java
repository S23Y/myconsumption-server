package org.starfishrespect.myconsumption.server.business.exceptions;

/**
 * Types of exception.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public enum ExceptionType {
    NOT_DEFINED,
    USER_NOT_FOUND,
    SENSOR_NOT_FOUND,
    SENSOR_EXISTS,
    NO_SENSOR_DEFINED,
    UNKNOWN_SENSOR_TYPE,
    CANNOT_CHANGE_SENSOR_TYPE,
    NOTHING_TO_EDIT,
    INVALID_SENSOR_SETTINGS,
    BAD_FORMAT_SENSOR_SETTINGS,
    TOKEN_EXISTS,
    TOKEN_NOT_FOUND,
    DATABASE_OPERATION_ERROR
}
