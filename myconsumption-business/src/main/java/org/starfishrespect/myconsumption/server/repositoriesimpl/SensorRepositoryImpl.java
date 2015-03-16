package org.starfishrespect.myconsumption.server.repositoriesimpl;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;

import java.util.List;

/**
 * Created by thibaud on 12.03.15.
 */
public class SensorRepositoryImpl implements SensorRepository {

    private MongoOperations mongoOperation;
    private String COLLECTION_NAME = "sensors";

    public SensorRepositoryImpl(MongoOperations mongoOperation) {
        this.mongoOperation = mongoOperation;
        this.init();
    }


    @Override
    public List<Sensor> getAllSensors() {
        return mongoOperation.findAll(Sensor.class, COLLECTION_NAME);
    }

    @Override
    public Sensor getSensor(String id) {
        if (mongoOperation.exists(idQuery(id), Sensor.class, COLLECTION_NAME)) {
            return mongoOperation.findOne(idQuery(id), Sensor.class, COLLECTION_NAME);
        } else {
            return null;
        }
    }

    @Override
    public Sensor insertSensor(Sensor sensor) {
        Criteria criteria = new Criteria("type").is(sensor.getType());
        Criteria settingsCriterias = null;
        for (String key : sensor.getSensorSettings().getKeys()) {
            if (settingsCriterias == null) {
                settingsCriterias = new Criteria("sensorSettings." + key).is(sensor.getSensorSettings().getValue(key));
            } else {
                settingsCriterias = new Criteria("sensorSettings." + key).is(sensor.getSensorSettings().getValue(key)).andOperator(settingsCriterias);
            }
        }
        if (settingsCriterias != null) {
            criteria.andOperator(settingsCriterias);
        }
        Query existingQuery = new Query(criteria);
        if (mongoOperation.exists(existingQuery, Sensor.class, COLLECTION_NAME)) {
            return mongoOperation.findOne(existingQuery, Sensor.class, COLLECTION_NAME);
        }
        mongoOperation.save(sensor, COLLECTION_NAME);
        return sensor;
    }

    @Override
    public boolean updateSensor(Sensor sensor) {
        mongoOperation.save(sensor, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean incrementUsageCount(String id) {
        Update update = new Update().inc("usageCount", 1);
        mongoOperation.updateFirst(idQuery(id), update, Sensor.class, COLLECTION_NAME);
        return true;
    }

    @Override
    public int getUsageCount(String id) {
        Sensor s = getSensor(id);
        if (s == null) {
            return -1;
        }
        return s.getUsageCount();
    }

    @Override
    public boolean decrementUsageCount(String id) {
        Update update = new Update().inc("usageCount", -1);
        mongoOperation.updateFirst(idQuery(id), update, Sensor.class, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean decrementUsageCountAndDeleteIfUnused(String id) {
        Update update = new Update().inc("usageCount", -1);
        mongoOperation.updateFirst(idQuery(id), update, Sensor.class, COLLECTION_NAME);
        Sensor s = getSensor(id);
        if (s.getUsageCount() <= 0) {
            deleteSensor(s.getId());
        }
        return true;
    }

    @Override
    public boolean deleteSensor(String id) {
        mongoOperation.remove(idQuery(id), COLLECTION_NAME);
        mongoOperation.dropCollection("sensor_" + id);
        return true;
    }

    @Override
    public boolean sensorExists(String id) {

        return mongoOperation.exists(idQuery(id), Sensor.class, COLLECTION_NAME);
    }

    @Override
    public void init() {
        if (!mongoOperation.collectionExists(COLLECTION_NAME)) {
            mongoOperation.createCollection(COLLECTION_NAME);
        }
    }

    @Override
    public void reset() {
        mongoOperation.dropCollection(COLLECTION_NAME);
        init();
    }

    private Query idQuery(String id) {
        return new Query(new Criteria("_id").is(id));
    }
}
