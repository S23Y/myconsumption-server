package org.starfishrespect.myconsumption.server.business.dto.flukso;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Class used to represent Flukso-specific settings for REST API
 */
@JsonAutoDetect
public class FluksoSensorSettingsDTO {
    @JsonProperty
    private String fluksoId;
    @JsonProperty
    private String token;

    public FluksoSensorSettingsDTO() {
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
}
