package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

/**
 * Created by thibaud on 16.03.15.
 */
public interface StatRepository extends MongoRepository<StatsOverPeriodsDTO, String>  {
}
