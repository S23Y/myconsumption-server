package org.starfishrespect.myconsumption.server.controllers;

/**
 * Created by thibaud on 30.03.15.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.*;
import org.starfishrespect.myconsumption.server.entities.Stat;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;


/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/stats")
public class StatController {
    @Autowired
    private StatRepository mStatRepository;

    @RequestMapping(value = "/sensor/{sensorId}", method = RequestMethod.GET)
    public List<Stat> getAllStats(@PathVariable String sensorId) {
        return mStatRepository.findBySensorId(sensorId);
    }
}
