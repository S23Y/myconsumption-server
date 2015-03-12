package org.starfishrespect.myconsumption.server.repositoriesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.exception.DaoException;
import org.starfishrespect.myconsumption.server.exception.ExceptionType;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepositoryCustom;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 12.03.15.
 */
public class ValuesRepositoryImpl implements ValuesRepositoryCustom {

    private final MongoOperations mongoOperation;
    private String sensor = null;
    private String collectionName = null;

    @Autowired
    public ValuesRepositoryImpl(MongoOperations operations) {
        Assert.notNull(operations, "MongoOperations must not be null!");
        this.mongoOperation = operations;
    }

    @Override
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
    public List<SensorDataset> getSensor(Date startTime, Date endTime) throws DaoException {
        Query timeQuery = new Query(new Criteria().andOperator(
                Criteria.where("timestamp").gte(startTime),
                Criteria.where("timestamp").lte(endTime)
        ));
        timeQuery.with(new Sort(Sort.Direction.ASC, "timestamp"));
        return mongoOperation.find(timeQuery, SensorDataset.class, collectionName);
    }


    private DaoException makeNoSensorException() {
        return new DaoException("You must set a sensor before doing any operation on the database.", ExceptionType.NO_SENSOR_DEFINED);
    }
}
