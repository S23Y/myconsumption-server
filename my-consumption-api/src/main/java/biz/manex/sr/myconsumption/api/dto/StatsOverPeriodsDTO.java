package biz.manex.sr.myconsumption.api.dto;
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

    public StatsOverPeriodsDTO(String sensorId) {
        mSensorId = sensorId;
    }

    public List<StatDTO> getStatDTOs() {
        return mStatDTOs;
    }

    public void setStatDTOs(List<StatDTO> statDTOs) {
        mStatDTOs = statDTOs;
    }
}
