package org.starfishrespect.myconsumption.server.business.sensors;

import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public interface SensorSettings {
    public List<String> getKeys();

    public String getValue(String key);
}
