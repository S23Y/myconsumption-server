package org.starfishrespect.myconsumption.server.business;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Patrick Herbeuval on 24/03/14.
 */
public class PasswordHash {

    private static final String PASSWORD_SALT = "MXSLT_SR_MYCO";

    public static String saltAndHash(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String salted = PASSWORD_SALT + password;
            messageDigest.update(salted.getBytes());
            return byteArrayToHex(messageDigest.digest());
        }
        // this will never happen, SHA-256 is bundled with Java
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
