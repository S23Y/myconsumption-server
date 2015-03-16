package org.starfishrespect.myconsumption.server.repositories;

import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.exception.DaoException;

import java.util.Date;
import java.util.List;

/**
 * Interface to access the values database
 */
public interface ValuesRepositoryCustom {

    /**
     * Initialises the database, creating all needed data structure if not existing
     */
    public void init() throws DaoException;

    /**
     * Resets the database. This will delete all the data present in it, so
     * be careful !
     */
    public void reset() throws DaoException;

    /**
     * Sets the sensor that you want to access or edit values
     * @param sensor the sensor id
     */
    public void setSensor(String sensor);

    /**
     * Inserts a value into the database. If the value is already present,
     * content of the database is updated
     *
     * @param value the value
     */
    public void insertOrUpdate(SensorDataset value) throws DaoException;

    /**
     * Insert all the values into the database. If the value is already present,
     * content of the database is updated
     *
     * @param values the values
     */
    public void insertOrUpdate(List<SensorDataset> values) throws DaoException;

    /**
     * Returns the values for one sensor for a given interval
     *
     * @param startTime the start of the interval
     * @param endTime   the end of the interval
     * @return the retrieved values
     * @throws org.starfishrespect.myconsumption.server.exception.DaoException
     */
    public List<SensorDataset> getSensor(Date startTime, Date endTime) throws DaoException;

    /**
     * Removes all the values from a given sensor
     *
     * @param sensor the sensor
     * @return true if the sensor has been correctly removed
     * @throws org.starfishrespect.myconsumption.server.exception.DaoException
     */
    public boolean removeSensor(String sensor) throws DaoException;

    /**
     * Returns the values for a given hour
     *
     * @param timestamp the hour
     * @return the values
     * @throws org.starfishrespect.myconsumption.server.exception.DaoException
     */
    public SensorDataset getOne(Date timestamp) throws DaoException;
}
