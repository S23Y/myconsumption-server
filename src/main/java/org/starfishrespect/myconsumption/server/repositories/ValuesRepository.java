package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 12.03.15.
 */
public interface ValuesRepository extends MongoRepository<SensorDataset, String> {
    //Query: { "$and" : [ { "timestamp" : { "$gte" : ?1}} , { "timestamp" : { "$lte" : ?2 }}]}, Fields: null, Sort: { "timestamp" : 1}

    @Query(value = "{timestamp : {$gte : ?0, $lte : ?1}}")
    public List<SensorDataset> getValuesForSensor(String id, Date startTime, Date endTime);

}