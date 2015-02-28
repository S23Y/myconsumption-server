package biz.manex.sr.myconsumption.business.sensors;

import biz.manex.sr.myconsumption.business.sensors.exceptions.RetrieveException;

import java.util.Date;

/**
 * abstract class used to retrieve data from a sensor
 */

public interface SensorRetriever {

    /**
     * Implement this function to retrieve as much information as possible from the sensor
     *
     * @return The retrieved data
     * @throws biz.manex.sr.myconsumption.business.sensors.exceptions.RetrieveException
     */
    public abstract SensorData getAllData() throws RetrieveException;

    /**
     * Implement this function to retrieve all data available since the given date
     *
     * @param startTime The time when we want to start to retrieve data.
     * @return The retrieved data
     * @throws RetrieveException
     */
    public abstract SensorData getDataSince(Date startTime) throws RetrieveException;

    /**
     * Implement this function to retrieve all data in a time interval.
     *
     * @param startTime Start of the interval
     * @param endTime   End of the interval. Must be higher of equal than startTime, and lower than actual time
     * @return The retrieved data
     * @throws RetrieveException
     */
    public abstract SensorData getData(Date startTime, Date endTime) throws RetrieveException;
}
