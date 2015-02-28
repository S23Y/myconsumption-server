package org.starfishrespect.myconsumption.server.api.services;

import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by thibaud on 27.01.15.
 */
@Path("/")
public interface StatService {

    /**
     * Returns all the stats of a given sensor
     *
     * @param sensor
     * @return
     */

    @GET
    @Path("sensor/{sensor}")
    @Produces(MediaType.APPLICATION_JSON)
    public StatsOverPeriodsDTO getAllStats(@PathParam("sensor") String sensor);

//    /**
//     * Returns the mean of the values of a given sensor
//     *
//     * @param sensor
//     * @return
//     */
//    @GET
//    @Path("mean/{sensor}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Integer meanForSensor(@PathParam("sensor") String sensor);
//
//
//    /**
//     * Returns the max of the values of a given sensor
//     *
//     * @param sensor
//     * @return
//     */
//    @GET
//    @Path("max/{sensor}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Integer maxForSensor(@PathParam("sensor") String sensor);
//
//    /**
//     * Returns the comparison of the values of a given sensor
//     *
//     * @param sensor
//     * @return
//     */
//    @GET
//    @Path("comp/{sensor}/duration/{duration}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Integer compDuration(@PathParam("sensor") String sensor,
//                                @PathParam("duration") int duration);
}
