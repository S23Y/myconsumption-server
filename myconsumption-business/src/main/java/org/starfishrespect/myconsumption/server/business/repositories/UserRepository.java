package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.User;

import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {;
    public List<User> findByNameAndPassword(String name, String password);
}
