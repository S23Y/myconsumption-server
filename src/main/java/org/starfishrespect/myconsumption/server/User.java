package org.starfishrespect.myconsumption.server;

public class User {

    private final long id;
    private final String name;

    public User(long id, String content) {
        this.id = id;
        this.name = content;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
