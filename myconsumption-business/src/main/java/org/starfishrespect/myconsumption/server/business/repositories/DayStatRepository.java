package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.DayStat;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 01.05.15.
 */
public interface DayStatRepository extends MongoRepository<DayStat, String> {
    List<DayStat> findBySensorId(String sensorId);
    List<DayStat> findBySensorIdAndDay(String sensorId, Date day);
}