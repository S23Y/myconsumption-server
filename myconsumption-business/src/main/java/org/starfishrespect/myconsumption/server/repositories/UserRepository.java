package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.entities.User;

import java.util.List;

/**
 * Created by thibaud on 16.03.15.
 */
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {;
}
