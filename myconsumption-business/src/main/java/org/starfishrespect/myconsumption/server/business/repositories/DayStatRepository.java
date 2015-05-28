package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.DayStat;

import java.util.Date;
import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public interface DayStatRepository extends MongoRepository<DayStat, String> {
    List<DayStat> findBySensorId(String sensorId);
    List<DayStat> findBySensorIdAndDay(String sensorId, Date day);
}