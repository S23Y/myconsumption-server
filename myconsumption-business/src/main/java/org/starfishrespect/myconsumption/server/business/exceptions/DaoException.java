package org.starfishrespect.myconsumption.server.business.exceptions;

/**
 * DAO exception
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class DaoException extends Exception {

    ExceptionType type = ExceptionType.NOT_DEFINED;

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, ExceptionType exceptionType) {
        super(message);
        this.type = exceptionType;
    }

    public DaoException(ExceptionType exceptionType) {
        super("");
        this.type = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return type;
    }
}
