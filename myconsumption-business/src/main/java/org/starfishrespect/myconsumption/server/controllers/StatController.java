package org.starfishrespect.myconsumption.server.controllers;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;
import org.starfishrespect.myconsumption.server.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.repositories.PeriodStatRepository;
import org.starfishrespect.myconsumption.server.stats.StatUtils;

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
        List<PeriodStat> periodStats = mPeriodStatRepository.findBySensorId(sensorId);
        List<StatDTO> stats = new ArrayList<>();

        Mapper mapper = new DozerBeanMapper();

        for (PeriodStat periodStat : periodStats) {
            // If the period stat is complete (= all the days are present)
            if (periodStat.getDaysInPeriod().size() == StatUtils.getNumberOfDaysInPeriod(periodStat.getPeriod()) {
                stats.add(mapper.map(periodStat, StatDTO.class));
            }
        }

        return stats;
    }
}
