package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.SensorDataset;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public interface ValuesRepository extends MongoRepository<SensorDataset, String>, ValuesRepositoryCustom {
}
