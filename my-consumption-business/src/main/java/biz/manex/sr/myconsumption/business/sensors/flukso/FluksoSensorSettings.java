package biz.manex.sr.myconsumption.business.sensors.flukso;

import biz.manex.sr.myconsumption.business.sensors.SensorSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage object for Flukso-specific sensor settings
 */
public class FluksoSensorSettings implements SensorSettings {
    private String token;
    private String fluksoId;

    public FluksoSensorSettings(String fluksoId, String token) {
        this.fluksoId = fluksoId;
        this.token = token;
    }

    public FluksoSensorSettings() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFluksoId() {
        return fluksoId;
    }

    public void setFluksoId(String fluksoId) {
        this.fluksoId = fluksoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FluksoSensorSettings that = (FluksoSensorSettings) o;

        if (fluksoId != null ? !fluksoId.equals(that.fluksoId) : that.fluksoId != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    @Override
    public List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        keys.add("fluksoId");
        return keys;
    }

    @Override
    public String getValue(String key) {
        switch (key) {
            case "fluksoId":
                return getFluksoId();
            default:
                return null;
        }

    }
}
