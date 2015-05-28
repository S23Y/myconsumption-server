package org.starfishrespect.myconsumption.server.business.repositories;

import org.starfishrespect.myconsumption.server.business.entities.User;
import org.starfishrespect.myconsumption.server.business.exceptions.DaoException;

import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public interface UserRepositoryCustom {
    /**
     * Tests the existence of an user
     *
     * @param name the name of this user
     * @return true if this users exists
     */
    public boolean userExists(String name);

    /**
     * Returns the user with a given name
     *
     * @param name the name of the user
     * @return the user, or null if not present in database
     */
    public User getUser(String name);

    /**
     * Returns a list of all users
     *
     * @return a list containing all users
     */
    public List<User> getAllUsers();

    /**
     * Returns the list of the users associated to a given sensor id.
     * @param sensorId a sensor id as a String
     * @return returns the list of the users associated to a given sensor id
     */
    public List<User> findBySensorId(String sensorId);

    /**
     * Insert an user in the database
     *
     * @param user the user
     * @return true if the user is inserted correctly, false if it already exists
     * @throws org.starfishrespect.myconsumption.server.business.exceptions.DaoException if the user is invalid (invalid name, null user, missing data, ...)
     */
    public boolean insertUser(User user) throws DaoException;

    /**
     * Updates an user
     *
     * @param user the user
     * @return true if correctly updated, false if the user doesn't exists
     * @throws org.starfishrespect.myconsumption.server.business.exceptions.DaoException if the user is invalid
     */
    public boolean updateUser(User user) throws DaoException;

    /**
     * Deletes an user from the database
     *
     * @param name the name of the user we want to delete
     * @return true if the user has been deleted, false if it doesn't exists
     */
    public boolean deleteUser(String name);

    /**
     * Initialises the database, creating all needed data structure if not existing
     */
    public void init();

    /**
     * Resets the database. This will delete all the data present in it, so
     * be careful !
     */
    public void reset();

}