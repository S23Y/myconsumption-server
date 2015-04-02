package org.starfishrespect.myconsumption.server.api.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

/**
 * Created by thibaud on 19.02.15.
 * REST representation of statistics
 *
 */
@JsonAutoDetect
public class StatsOverPeriodsDTO {
    private String mSensorId;
    private List<StatDTO> mStatDTOs;

    public StatsOverPeriodsDTO() {
    }

    public void setSensorId(String sensorId) {
        mSensorId = sensorId;
    }

    public String getSensorId() {
        return mSensorId;
    }

    public List<StatDTO> getStatDTOs() {
        return mStatDTOs;
    }

    public void setStatDTOs(List<StatDTO> statDTOs) {
        mStatDTOs = statDTOs;
    }
}
