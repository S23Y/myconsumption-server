package biz.manex.sr.myconsumption.business.dao;

import biz.manex.sr.myconsumption.business.sensors.Sensor;

import java.util.List;

/**
 * Created by Patrick Herbeuval on 16/04/14.
 */
public interface SensorDao {

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
