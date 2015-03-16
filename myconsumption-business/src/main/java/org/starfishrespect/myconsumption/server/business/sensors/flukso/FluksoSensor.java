package org.starfishrespect.myconsumption.server.business.sensors.flukso;

import org.starfishrespect.myconsumption.server.entities.Sensor;

/**
 * implementation of the Sensor class for Flukso sensor
 */
public class FluksoSensor extends Sensor {

    public FluksoSensor() {
        super();
    }

    public FluksoSensor(String name, String fluksoId, String token) {
        super(name, "flukso");
        this.sensorSettings = new FluksoSensorSettings(fluksoId, token);
    }

    public String getFluksoId() {
        return ((FluksoSensorSettings) sensorSettings).getFluksoId();
    }

    public String getToken() {
        return ((FluksoSensorSettings) sensorSettings).getToken();
    }

    public void setToken(String token) {
        ((FluksoSensorSettings) sensorSettings).setToken(token);
    }

}
