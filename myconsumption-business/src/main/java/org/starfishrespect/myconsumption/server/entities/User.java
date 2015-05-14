package org.starfishrespect.myconsumption.server.entities;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class User {
    private static final Logger mLogger = LoggerFactory.getLogger(User.class);


    @Id
    private String id;

    private String name;
    private String password;
    private byte[] salt;
    private List<String> sensors;
    private String registerId;

    public User() {}

    public User(String name, String password) {
        this.name = name;
        this.salt = getRandomSalt();
        this.password = hashAndSalt(password, salt);
        sensors = new ArrayList<>();
    }

    private byte[] getRandomSalt() {
        // Generate a random salt
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);

        return salt;
    }

    /**
     * Return a Base64 encoded String of hash(hashedPassword + salt)
     * @param password the password to hash
     * @return Return a Base64 encoded String of hash(hashedPassword + salt)
     */
    public static String hashAndSalt(String password, byte[] salt) {
        byte[] hashPwd = Base64.decodeBase64(password);

        // Concatenate the salt and the hash
        byte[] hashPwdSalt = new byte[hashPwd.length + salt.length];
        System.arraycopy(hashPwd, 0, hashPwdSalt, 0, hashPwd.length);
        System.arraycopy(salt, 0, hashPwdSalt, hashPwd.length, salt.length);

        // Hash everything and return
        return Base64.encodeBase64String(sha256(hashPwdSalt));
    }


    private static byte[] sha256(byte[] input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            mLogger.error(e.toString());
        }
        byte[] hash = new byte[0];
        if (digest != null) {
            hash = digest.digest(input);
        }
        return hash;
    }


    @Override
    public String toString() {
        return String.format(
                "User [id=%s, name='%s', sensors='%s']",
                id, name, this.printSensors());
    }

    private String printSensors() {
        return sensors == null ? "" : sensors.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(String sensorID) {
        sensors.add(sensorID);
    }

    public void removeSensor(String sensorId) {
        for (int i = 0; i < sensors.size(); i ++) {
            if (sensors.get(i).equals(sensorId)) {
                sensors.remove(i);
                return;
            }
        }
    }
}