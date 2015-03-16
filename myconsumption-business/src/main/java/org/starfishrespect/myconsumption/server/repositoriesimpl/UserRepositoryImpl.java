package org.starfishrespect.myconsumption.server.repositoriesimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.starfishrespect.myconsumption.server.entities.User;
import org.starfishrespect.myconsumption.server.exception.DaoException;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import java.util.List;

/**
 * Implementation of UsersDao object, using a MongoDB database
 */
public class UserRepositoryImpl implements UserRepository {

    private MongoOperations mongoOperation;
    private String COLLECTION_NAME = "users";
    private static String USERNAME_PATTERN = "[A-Za-z][\\.\\w_-]*";

    @Autowired
    public UserRepositoryImpl(MongoOperations mongoOperation) {
        this.mongoOperation = mongoOperation;
    }

    @Autowired
    public UserRepositoryImpl(MongoOperations mongoOperation, String collectionName) {
        this.mongoOperation = mongoOperation;
        this.COLLECTION_NAME = collectionName;
        this.init();
    }

    /**
     * Set the collection name to store the user. Default is "users"
     *
     * @param newCollectionName the new collection name to use, or null to
     *                          reset to default
     */
    public void setCollectionName(String newCollectionName) {
        if (newCollectionName == null) {
            this.COLLECTION_NAME = "users";
        } else {
            this.COLLECTION_NAME = newCollectionName;
        }
    }

    @Override
    public boolean userExists(String name) {
        Query existingQuery = new Query(new Criteria("name").is(name));
        return mongoOperation.exists(existingQuery, User.class, COLLECTION_NAME);
    }

    @Override
    public List<User> getAllUsers() {
        return mongoOperation.findAll(User.class, COLLECTION_NAME);
    }

    @Override
    public User getUser(String name) {
        Query existingQuery = new Query(new Criteria("name").is(name));
        if (mongoOperation.exists(existingQuery, User.class, COLLECTION_NAME)) {
            return mongoOperation.findOne(existingQuery, User.class, COLLECTION_NAME);
        } else {
            return null;
        }
    }

    @Override
    public boolean insertUser(User user) throws DaoException {
        if (userExists(user.getName())) {
            return false;
        }
        if (!user.getName().matches(USERNAME_PATTERN)) {
            throw new DaoException("Username must start by a letter and contain " +
                    "only letters, numbers, -, _ and dots.");
        }
        mongoOperation.save(user, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean updateUser(User user) throws DaoException {
        if (!userExists(user.getName())) {
            return false;
        }
        mongoOperation.save(user, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean deleteUser(String name) {
        if (!userExists(name)) {
            return false;
        }
        mongoOperation.remove(new Query(new Criteria("name").is(name)), COLLECTION_NAME);
        mongoOperation.dropCollection("user_" + name);
        return true;
    }

    @Override
    public void init() {
        if (!mongoOperation.collectionExists(COLLECTION_NAME)) {
            mongoOperation.createCollection(COLLECTION_NAME);
            mongoOperation.indexOps(User.class).ensureIndex(new Index("name", Sort.Direction.ASC).unique());
        }
    }

    @Override
    public void reset() {
        mongoOperation.dropCollection(COLLECTION_NAME);
        init();
    }
}