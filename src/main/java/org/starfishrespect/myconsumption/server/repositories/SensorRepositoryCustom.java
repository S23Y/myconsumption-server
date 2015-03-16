package org.starfishrespect.myconsumption.server.repositories;

import org.starfishrespect.myconsumption.server.entities.Sensor;

/**
 * Created by thibaud on 12.03.15.
 */
public interface SensorRepositoryCustom {
    /**
     * Initialises the database, creating all needed data structure if not existing
     */
    public void init();

    public boolean incrementUsageCount(String id);

    public int getUsageCount(String id);

    public boolean decrementUsageCount(String id);

    public boolean decrementUsageCountAndDeleteIfUnused(String id);

    public boolean deleteSensor(String id);

    public Sensor getSensor(String id);

    public Sensor insertSensor(Sensor sensor);

}
