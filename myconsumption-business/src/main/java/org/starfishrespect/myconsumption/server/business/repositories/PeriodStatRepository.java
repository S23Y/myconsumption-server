package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.business.entities.PeriodStat;

import java.util.List;

/**
 * Created by thibaud on 16.03.15.
 */
public interface PeriodStatRepository extends MongoRepository<PeriodStat, String>  {
    List<PeriodStat> findBySensorId(String sensorId);
    List<PeriodStat> findByPeriod(Period p);
    List<PeriodStat> findBySensorIdAndPeriod(String sensorId, Period p);
}
