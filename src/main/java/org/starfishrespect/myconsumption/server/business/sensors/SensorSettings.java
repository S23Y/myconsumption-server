package org.starfishrespect.myconsumption.server.business.sensors;

import java.util.List;

/**
 * Created by Patrick Herbeuval on 16/04/14.
 */
public interface SensorSettings {
    public List<String> getKeys();

    public String getValue(String key);
}
