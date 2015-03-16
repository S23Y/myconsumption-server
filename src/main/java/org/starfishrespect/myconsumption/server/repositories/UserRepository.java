package org.starfishrespect.myconsumption.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;

/**
 * Created by thibaud on 11.03.15.
 */
public interface UserRepository extends MongoRepository<UserDTO, String> {

    public UserDTO findByName(String name);

}