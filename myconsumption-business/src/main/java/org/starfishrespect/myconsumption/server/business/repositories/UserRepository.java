package org.starfishrespect.myconsumption.server.business.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.business.entities.User;

import java.util.List;

/**
 * Created by thibaud on 16.03.15.
 */
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {;
    public List<User> findByNameAndPassword(String name, String password);
}
