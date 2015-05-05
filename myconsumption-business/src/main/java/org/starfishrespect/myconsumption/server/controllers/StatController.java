package org.starfishrespect.myconsumption.server.controllers;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;
import org.starfishrespect.myconsumption.server.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.repositories.PeriodStatRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by thibaud on 11.03.15.
 */
@RestController
@RequestMapping("/stats")
public class StatController {
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;

    @RequestMapping(value = "/sensor/{sensorId}", method = RequestMethod.GET)
    public List<StatDTO> getAllStats(@PathVariable String sensorId) {
        List<PeriodStat> periods = mPeriodStatRepository.findBySensorId(sensorId);
        List<StatDTO> stats = new ArrayList<>();

        Mapper mapper = new DozerBeanMapper();

        for (PeriodStat period : periods) {
            stats.add(mapper.map(period, StatDTO.class));
        }
        return stats;
    }
}
