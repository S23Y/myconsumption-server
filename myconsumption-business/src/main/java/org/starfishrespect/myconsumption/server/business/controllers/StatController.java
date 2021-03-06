package org.starfishrespect.myconsumption.server.business.controllers;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;
import org.starfishrespect.myconsumption.server.business.entities.PeriodStat;
import org.starfishrespect.myconsumption.server.business.entities.User;
import org.starfishrespect.myconsumption.server.business.repositories.PeriodStatRepository;
import org.starfishrespect.myconsumption.server.business.repositories.UserRepository;
import org.starfishrespect.myconsumption.server.business.stats.StatUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Rest controller for the stats.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
@RestController
@RequestMapping("/stats")
public class StatController {
    @Autowired
    private PeriodStatRepository mPeriodStatRepository;

    @Autowired
    private UserRepository mUserRepository;

    @RequestMapping(value = "/sensor/{sensorId}", method = RequestMethod.GET)
    public List<StatDTO> getAllStats(Principal principal, @PathVariable String sensorId) {
        List<User> users = mUserRepository.findBySensorId(sensorId);

        // Check if this user can access this resource
        boolean allowed = false;

        for(User user : users) {
            if (principal.getName().equals(user.getName()))
                allowed = true;
        }

        if (!allowed)
            return null;


        // Get all stats
        List<PeriodStat> periodStats = mPeriodStatRepository.findBySensorId(sensorId);
        List<StatDTO> stats = new ArrayList<>();

        Mapper mapper = new DozerBeanMapper();

        for (PeriodStat periodStat : periodStats) {
            // If the period stat is complete (= all the days are present)
            if (periodStat.getDaysInPeriod().size() == StatUtils.getNumberOfDaysInPeriod(periodStat.getPeriod())) {
                stats.add(mapper.map(periodStat, StatDTO.class));
            }
        }

        return stats;
    }

//    @RequestMapping(value = "/sensor/{sensorId}/period/{period}", method = RequestMethod.GET)
//    public List<PeriodStat> getAllStatsByPeriod(@PathVariable String sensorId, @PathVariable String period) {
//        switch (period) {
//            case "ALLTIME":
//                return mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, Period.ALLTIME);
//            case "DAY":
//                return mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, Period.DAY);
//            case "WEEK":
//                return mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, Period.WEEK);
//            case "MONTH":
//                return mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, Period.MONTH);
//            case "YEAR":
//                return mPeriodStatRepository.findBySensorIdAndPeriod(sensorId, Period.YEAR);
//            default:
//                return new ArrayList<>();
//        }
//    }
}
