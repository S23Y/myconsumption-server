package org.starfishrespect.myconsumption.server.business;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Arrays;

/**
* Created by thibaud on 28.05.15.
*/
@Configuration
public class MongoConfiguration {

    /**
     * DB connection Factory
     *
     * @return a ready to use MongoDbFactory
     */
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {

        // Set credentials
        MongoCredential credential = MongoCredential.createMongoCRCredential("S23Yadmin", "test", "mongoS23Y".toCharArray());
        ServerAddress serverAddress = new ServerAddress("127.0.0.1", 27017);

        // Mongo Client
        MongoClient mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

        // Mongo DB Factory
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(
                mongoClient, "test");

        return simpleMongoDbFactory;
    }

    /**
     * Template ready to use to operate on the database
     *
     * @return Mongo Template ready to use
     */
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }


}
