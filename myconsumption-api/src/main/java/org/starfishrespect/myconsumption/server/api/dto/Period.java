package org.starfishrespect.myconsumption.server.api.dto;

/**
 * Representation of a stat period.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public enum Period {
    ALLTIME(0),
    DAY(1),
    WEEK(2),
    MONTH(3),
    YEAR(4);

    private final int value;

    private Period(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
