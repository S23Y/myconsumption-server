package org.starfishrespect.myconsumption.server.business.repositories.repositoriesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.starfishrespect.myconsumption.server.business.entities.MinuteValues;
import org.starfishrespect.myconsumption.server.business.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.business.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.business.exceptions.ExceptionType;
import org.starfishrespect.myconsumption.server.business.repositories.ValuesRepositoryCustom;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by thibaud on 12.03.15.
 */
public class ValuesRepositoryImpl implements ValuesRepositoryCustom {

    private String collectionName = null;
    private String sensor = null;
    private MongoOperations mongoOperation;
    private MongoOperations adminMongoOperation;
    private String shardDbName = "";

//    @Autowired(required=false)
    @Autowired
    public ValuesRepositoryImpl(MongoOperations operations) {
        Assert.notNull(operations, "MongoOperations must not be null!");
        this.mongoOperation = operations;
    }

//    //@Autowired(required=false)
//    @Autowired
//    public ValuesRepositoryImpl(MongoOperations mongoOperation, MongoOperations adminMongoOperation, String shardDbName) {
//        this.mongoOperation = mongoOperation;
//        this.adminMongoOperation = adminMongoOperation;
//        this.shardDbName = shardDbName;
//    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
        this.collectionName = "sensor_" + sensor;
    }

    @Override
    public void init() throws DaoException {
        if (collectionName == null) {
            throw makeNoSensorException();
        }
        if (!mongoOperation.collectionExists(collectionName)) {
            mongoOperation.createCollection(collectionName);
            mongoOperation.indexOps(collectionName).ensureIndex(new Index("timestamp", Sort.Direction.ASC).unique());

            // Distribute the database across multiple servers (useful in prod)
/*            BasicDBObject shardCommand = new BasicDBObject("shardcollection", shardDbName + "." + collectionName);
            shardCommand.put("key", new BasicDBObject("timestamp", 1));
            CommandResult result = adminMongoOperation.executeCommand(shardCommand);
            System.out.println(result.toString()); */
        }
    }

    @Override
    public void reset() throws DaoException {
        if (collectionName != null) {
            mongoOperation.dropCollection(collectionName);
            init();
        }
    }

    @Override
    public void insertOrUpdate(SensorDataset value) throws DaoException {
        Update update = new Update();
        Query existingQuery = new Query(new Criteria("timestamp").is(value.getTimestamp()));

        if (mongoOperation.exists(existingQuery, SensorDataset.class, collectionName)) {
            TreeMap<Integer, MinuteValues> minuteValues = value.getValues();
            for (Integer minuteTs : minuteValues.keySet()) {
                Query existingMinute = new Query(new Criteria().andOperator(
                        Criteria.where("timestamp").is(value.getTimestamp()),
                        Criteria.where("values." + minuteTs)
                ));
                MinuteValues minute;
                if (mongoOperation.exists(existingMinute, MinuteValues.class, collectionName)) {
                    minute = mongoOperation.findOne(existingMinute, MinuteValues.class, collectionName);
                    minute.merge(minuteValues.get(minuteTs));
                } else {
                    minute = minuteValues.get(minuteTs);
                }
                update.set("values." + minuteTs, minute);
            }
            mongoOperation.updateFirst(existingQuery, update, collectionName);
        } else {
            mongoOperation.save(value, collectionName);
        }
    }

    @Override
    public void insertOrUpdate(List<SensorDataset> values) throws DaoException {
        if (collectionName == null) {
            throw makeNoSensorException();
        }
        for (SensorDataset value : values) {
            insertOrUpdate(value);
        }
    }

    @Override
    public List<SensorDataset> getSensor(Date startTime, Date endTime) throws DaoException {
        Query timeQuery = new Query(new Criteria().andOperator(
                Criteria.where("timestamp").gte(startTime),
                Criteria.where("timestamp").lte(endTime)
        ));
        timeQuery.with(new Sort(Sort.Direction.ASC, "timestamp"));
        return mongoOperation.find(timeQuery, SensorDataset.class, collectionName);
    }

    @Override
    public SensorDataset getOne(Date timestamp) throws DaoException {
        Query timeQuery = new Query(Criteria.where("timestamp").is(timestamp));
        return mongoOperation.findOne(timeQuery, SensorDataset.class, collectionName);
    }

    @Override
    public boolean removeSensor(String sensor) throws DaoException {
        if (collectionName != null) {
            mongoOperation.dropCollection(collectionName);
            return true;
        }
        return false;
    }

    private DaoException makeNoSensorException() {
        return new DaoException("You must set a sensor before doing any operation on the database.", ExceptionType.NO_SENSOR_DEFINED);
    }
}
