package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.entities.Stat;

import java.util.List;

/**
 * Created by thibaud on 16.03.15.
 */
public interface StatRepository extends MongoRepository<Stat, String>  {
    List<Stat> findBySensorId(String sensorId);
    List<Stat> findBySensorIdAndPeriod(String sensorId, Period p);
}
