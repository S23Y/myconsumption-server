package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.SensorDataset;

/**
 * Created by thibaud on 16.03.15.
 */
public interface ValuesRepository extends MongoRepository<SensorDataset, String>, ValuesRepositoryCustom {
}
