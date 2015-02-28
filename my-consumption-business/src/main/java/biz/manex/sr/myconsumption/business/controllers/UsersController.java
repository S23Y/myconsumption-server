package biz.manex.sr.myconsumption.business.controllers;

import biz.manex.sr.myconsumption.business.dao.SensorDao;
import biz.manex.sr.myconsumption.business.dao.UsersDao;
import biz.manex.sr.myconsumption.business.datamodel.Token;
import biz.manex.sr.myconsumption.business.datamodel.User;
import biz.manex.sr.myconsumption.business.exception.DaoException;
import biz.manex.sr.myconsumption.business.exception.ExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller for users
 */
@Component
public class UsersController {

    @Autowired
    private UsersDao usersDao;
    @Autowired
    private SensorDao sensorDao;

    public boolean create(String username, String password) {
        User user = new User(username);
        user.setPassword(password);
        if (usersDao.userExists(user.getName())) {
            return false;
        } else {
            try {
                usersDao.insertUser(user);
                return true;
            } catch (DaoException e) {
                return false;
            }
        }
    }

    public User get(String username) {
        return usersDao.getUser(username);
    }

    public boolean userExists(String username) {
        return usersDao.userExists(username);
    }

    public boolean delete(String username) {
        return usersDao.deleteUser(username);
    }

    public void addSensor(String username, String sensorId) throws DaoException {
        if (!usersDao.userExists(username)) {
            throw new DaoException(ExceptionType.USER_NOT_FOUND);
        }
        if (!sensorDao.sensorExists(sensorId)) {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
        User user = usersDao.getUser(username);
        if (user.getSensors().contains(sensorId)) {
            throw new DaoException(ExceptionType.SENSOR_EXISTS);
        }
        user.getSensors().add(sensorId);
        usersDao.updateUser(user);
        sensorDao.incrementUsageCount(sensorId);
    }

    public void removeSensor(String username, String sensorId) throws DaoException {
        User user = usersDao.getUser(username);
        if (user == null) {
            throw new DaoException(ExceptionType.USER_NOT_FOUND);
        }
        if (!user.getSensors().remove(sensorId)) {
            throw new DaoException(ExceptionType.SENSOR_NOT_FOUND);
        }
        usersDao.updateUser(user);
        sensorDao.decrementUsageCount(sensorId);
    }

    public void addToken(String username, String deviceType, String token) throws DaoException {
        User user = usersDao.getUser(username);
        if (user != null) {
            if (user.addToken(new Token(deviceType, token))) {
                usersDao.updateUser(user);
            } else {
                throw new DaoException(ExceptionType.TOKEN_EXISTS);
            }
        } else {
            throw new DaoException(ExceptionType.USER_NOT_FOUND);
        }
    }

    public void deleteToken(String username, String token) throws DaoException {
        User user = usersDao.getUser(username);
        if (user != null) {
            if (user.removeToken(token)) {
                try {
                    usersDao.updateUser(user);
                } catch (DaoException e) {
                    System.out.println("e");
                    e.printStackTrace();
                    throw e;
                }

            } else {
                throw new DaoException(ExceptionType.TOKEN_NOT_FOUND);
            }
        } else {
            throw new DaoException(ExceptionType.USER_NOT_FOUND);
        }
    }
}
