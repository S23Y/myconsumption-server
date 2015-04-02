package org.starfishrespect.myconsumption.server.repositories.repositoriesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.starfishrespect.myconsumption.server.entities.MinuteValues;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepositoryCustom;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepository;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by thibaud on 12.03.15.
 */
public class SensorRepositoryImpl implements SensorRepositoryCustom {

    private MongoOperations mongoOperation;
    private String COLLECTION_NAME = "sensors";

    @Autowired
    private ValuesRepository mValuesRepository;

    @Autowired
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

    @Override
    public List<List<Integer>> getValues(String sensorId, int startTime, int endTime) throws DaoException {
        if (endTime == 0)
            endTime = Integer.MAX_VALUE;
        if (startTime < 0 || endTime < 0 || startTime > endTime) {
            throw new BadRequestException();
        }

        if (!sensorExists(sensorId))
            throw new NotFoundException();

        int effectiveStart = startTime - startTime % 3600;

        mValuesRepository.setSensor(sensorId);
        List<SensorDataset> daoValues = mValuesRepository.getSensor(new Date(((long) effectiveStart) * 1000L),
                new Date(((long) endTime) * 1000L));

        List<List<Integer>> values = new ArrayList<>();

        for (SensorDataset value : daoValues) {
            int start = (int) (value.getTimestamp().getTime() / 1000);
            TreeMap<Integer, MinuteValues> v = value.getValues();
            if (v == null) {
                continue;
            }
            for (int key : v.keySet()) {
                for (int second : v.get(key).containedSeconds()) {
                    int time = start + key * 60 + second;
                    if (time < startTime || time > endTime) {
                        continue;
                    }
                    List<Integer> item = new ArrayList<>();
                    item.add(time);
                    item.add(value.getValues().get(key).getValue(second));
                    values.add(item);
                }
            }
        }
        return values;
    }

    private Query idQuery(String id) {
        return new Query(new Criteria("_id").is(id));
    }
}
