package org.starfishrespect.myconsumption.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

@SpringBootApplication
//public class Application implements CommandLineRunner {
public class Application {

/*    @Autowired
    private UserRepository repository;*/

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

/*    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

*//*        // save a couple of customers
        repository.save(new User("Alice"));
        repository.save(new User("Bob"));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }
        System.out.println();

        // fetch an individual user
        System.out.println("User found with findByName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByName("Alice"));*//*

    }*/
}