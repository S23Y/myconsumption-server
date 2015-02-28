package biz.manex.sr.myconsumption.api.services;

import biz.manex.sr.myconsumption.api.dto.SimpleResponseDTO;
import biz.manex.sr.myconsumption.api.dto.UserDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by pat on 20/03/14.
 */
@Path("/")
public interface UserService {

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO get(@PathParam("username") String username);

    @POST
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO put(@PathParam("username") String username,
                                 @FormParam("password") @DefaultValue("") String password);


    @POST
    @Path("{username}/sensor/{sensorid}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO addSensor(@PathParam("username") String username,
                                       @PathParam("sensorid") String sensorId);

    @DELETE
    @Path("{username}/sensor/{sensorid}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO removeSensor(@PathParam("username") String username,
                                          @PathParam("sensorid") String sensorId);


    @DELETE
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO deleteUser(@PathParam("username") String username);

    @POST
    @Path("{username}/token/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO pushToken(@PathParam("username") String username,
                                       @FormParam("device_type") String deviceType,
                                       @PathParam("token") String token);

    @DELETE
    @Path("{username}/token/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleResponseDTO deleteToken(@PathParam("username") String username,
                                         @PathParam("token") String token);

}
