package org.starfishrespect.myconsumption.server.business.datamodel;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the information about an user (its name and its sensors)
 */
public class User {
    @Id
    private String id;
    private String name;
    private String password;
    private List<String> sensors;
    private List<Token> tokens;

    public User() {
        sensors = new ArrayList<>();
        tokens = new ArrayList<>();
    }

    public User(String name) {
        this();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(String sensor) {
        this.sensors.add(sensor);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public boolean addToken(Token token) {
        for (Token t : tokens) {
            if (t.equals(token)) {
                return false;
            }
        }
        tokens.add(token);
        return true;
    }

    public boolean removeToken(String token) {
        for (Token t : tokens) {
            if (t.isThisId(token)) {
                tokens.remove(t);
                return true;
            }
        }
        return false;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", sensors=" + sensors.toString() +
                '}';
    }
}
