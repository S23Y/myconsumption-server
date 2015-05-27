package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.Sensor;

/**
 * Created by thibaud on 16.03.15.
 */
public interface SensorRepository extends MongoRepository<Sensor, String>, SensorRepositoryCustom  {
}
