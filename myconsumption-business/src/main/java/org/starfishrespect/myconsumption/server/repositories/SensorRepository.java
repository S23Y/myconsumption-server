package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.entities.Sensor;


/**
 * Created by thibaud on 11.03.15.
 */
public interface SensorRepository extends MongoRepository<Sensor, String>, SensorRepositoryCustom {
}
