package biz.manex.sr.myconsumption.business.dao;

import biz.manex.sr.myconsumption.business.datamodel.User;
import biz.manex.sr.myconsumption.business.exception.DaoException;

import java.util.List;

/**
 * Interface to access to user database
 */
public interface UsersDao {
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
     * Insert an user in the database
     *
     * @param user the user
     * @return true if the user is inserted correctly, false if it already exists
     * @throws biz.manex.sr.myconsumption.business.exception.DaoException if the user is invalid (invalid name, null user, missing data, ...)
     */
    public boolean insertUser(User user) throws DaoException;

    /**
     * Updates an user
     *
     * @param user the user
     * @return true if correctly updated, false if the user doesn't exists
     * @throws biz.manex.sr.myconsumption.business.exception.DaoException if the user is invalid
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
