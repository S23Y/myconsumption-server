package org.starfishrespect.myconsumption.server.business.repositories;

import org.starfishrespect.myconsumption.server.business.entities.Sensor;
import org.starfishrespect.myconsumption.server.business.exceptions.DaoException;

import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent based on Patrick's implementation.
 */
public interface SensorRepositoryCustom {
    public List<Sensor> getAllSensors();

    public Sensor getSensor(String id);

    /**
     * Insert the sensor in the database if it doesn't exists. Existance
     * is checked on the type and specific parameters, so if the same
     * sensor is added by two users, its data won't be duplicated
     *
     * @param sensor the sensor to insert
     * @return the new inserted sensor, or an existing sensor if it already existed
     */
    public Sensor insertSensor(Sensor sensor);

    public boolean updateSensor(Sensor sensor);

    public boolean incrementUsageCount(String id);

    public int getUsageCount(String id);

    public boolean decrementUsageCount(String id);

    public boolean decrementUsageCountAndDeleteIfUnused(String id);

    public boolean deleteSensor(String id);

    public boolean sensorExists(String id);

    public List<List<Integer>> getValues(String sensorId, int startTime, int endTime) throws DaoException;

    /**
     * Initialises the database, creating all needed data structure if not existing
     */
    public void init();

    /**
     * Resets the database. This will delete all the data present in it, so
     * be careful !
     */
    public void reset();

}
