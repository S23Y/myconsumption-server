package biz.manex.sr.myconsumption.business.exception;

/**
 * Created by pat on 19/03/14.
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
