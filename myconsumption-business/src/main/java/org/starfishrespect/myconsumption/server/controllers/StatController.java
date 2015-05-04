package org.starfishrespect.myconsumption.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.repositories.StatRepository;

import java.util.List;


/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/stats")
public class StatController {
    @Autowired
    private StatRepository mStatRepository;

    @RequestMapping(value = "/sensor/{sensorId}", method = RequestMethod.GET)
    public List<PeriodStat> getAllStats(@PathVariable String sensorId) {
        return mStatRepository.findBySensorId(sensorId);
    }
}
