package org.starfishrespect.myconsumption.server.api.dto;

/**
 * Created by thibaud on 20.02.15.
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
