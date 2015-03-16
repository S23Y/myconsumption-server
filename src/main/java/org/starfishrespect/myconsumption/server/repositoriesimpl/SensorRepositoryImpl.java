package org.starfishrespect.myconsumption.server.repositoriesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.starfishrespect.myconsumption.server.api.dto.SensorDTO;
import org.starfishrespect.myconsumption.server.repositories.SensorRepositoryCustom;

/**
 * Created by thibaud on 12.03.15.
 */
public class SensorRepositoryImpl implements SensorRepositoryCustom {

    private MongoOperations mongoOperation;
    private String COLLECTION_NAME = "sensors";

    @Autowired
    public SensorRepositoryImpl(MongoOperations mongoOperation) {
        Assert.notNull(mongoOperation, "MongoOperations must not be null!");
        this.mongoOperation = mongoOperation;
        this.init();
    }

    @Override
    public void init() {
        if (!mongoOperation.collectionExists(COLLECTION_NAME)) {
            mongoOperation.createCollection(COLLECTION_NAME);
        }
    }


    @Override
    public boolean incrementUsageCount(String id) {
        Update update = new Update().inc("usageCount", 1);
        mongoOperation.updateFirst(idQuery(id), update, SensorDTO.class, COLLECTION_NAME);
        return true;
    }

    @Override
    public int getUsageCount(String id) {
        SensorDTO s = getSensor(id);
        if (s == null) {
            return -1;
        }
        return s.getUsageCount();
    }

    @Override
    public boolean decrementUsageCount(String id) {
        Update update = new Update().inc("usageCount", -1);
        mongoOperation.updateFirst(idQuery(id), update, SensorDTO.class, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean decrementUsageCountAndDeleteIfUnused(String id) {
        Update update = new Update().inc("usageCount", -1);
        mongoOperation.updateFirst(idQuery(id), update, SensorDTO.class, COLLECTION_NAME);
        SensorDTO s = getSensor(id);
        if (s.getUsageCount() <= 0) {
            deleteSensor(s.getId());
        }
        return true;
    }

    private Query idQuery(String id) {
        return new Query(new Criteria("_id").is(id));
    }

    @Override
    public boolean deleteSensor(String id) {
        mongoOperation.remove(idQuery(id), COLLECTION_NAME);
        mongoOperation.dropCollection("sensor_" + id);
        return true;
    }

    @Override
    public SensorDTO getSensor(String id) {
        if (mongoOperation.exists(idQuery(id), SensorDTO.class, COLLECTION_NAME)) {
            return mongoOperation.findOne(idQuery(id), SensorDTO.class, COLLECTION_NAME);
        } else {
            return null;
        }
    }
    @Override
    public SensorDTO insertSensor(SensorDTO sensor) {
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
        if (mongoOperation.exists(existingQuery, SensorDTO.class, COLLECTION_NAME)) {
            return mongoOperation.findOne(existingQuery, SensorDTO.class, COLLECTION_NAME);
        }
        mongoOperation.save(sensor, COLLECTION_NAME);
        return sensor;
    }

}
