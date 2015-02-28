package biz.manex.sr.myconsumption.api.services;

import biz.manex.sr.myconsumption.api.dto.SensorDTO;
import biz.manex.sr.myconsumption.api.dto.SimpleResponseDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by pat on 20/03/14.
 */
@Path("/")
public interface SensorService {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorDTO> getAllSensors();

    @GET
    @Path("{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public SensorDTO get(@PathParam("sensor") String sensorId);

    /**
     * Returns the values from a given sensor
     *
     * @param sensor
     * @param startTime
     * @param endTime
     * @return
     */
    @GET
    @Path("{sensor}/data")
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<Integer>> valuesForSensor(@PathParam("sensor") String sensor,
                                               @QueryParam("start") @DefaultValue("0") int startTime,
                                               @QueryParam("end") @DefaultValue("0") int endTime);


    @POST
    @Path("{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO editSensor(@PathParam("sensor") String sensorId,
                                        @FormParam("name") String name,
                                        @FormParam("settings") @DefaultValue("") String settings);

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO addSensor(@FormParam("type") @DefaultValue("") String sensorType,
                                       @FormParam("settings") @DefaultValue("") String settings,
                                       @FormParam("name") @DefaultValue("") String name,
                                       @FormParam("user") @DefaultValue("") String linkToUser);

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO clear();

    @DELETE
    @Path("{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO removeSensor(@PathParam("sensor") String sensorId);

}
