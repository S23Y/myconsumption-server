package org.starfishrespect.myconsumption.server.business.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to store all values for a 1 minute interval
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class MinuteValues {

    public static final int VALUE_EMPTY = -1;

    // value when the seconds are 0, used when we only one 1 measurement
    // for the minute, to avoid using and storing the hashmap
    private int zero = VALUE_EMPTY;
    private HashMap<Integer, Integer> seconds = null;

    public void put(int second, int value) {
        if (second == 0) {
            zero = value;
            if (seconds != null) {
                seconds.put(second, value);
            }
        } else {
            if (seconds == null) {
                seconds = new HashMap<Integer, Integer>();
                if (zero != VALUE_EMPTY) {
                    seconds.put(0, zero);
                }
            }
            seconds.put(second, value);
        }
    }

    public int getValue(int second) {
        if (second == 0) {
            return zero;
        } else {
            if (seconds == null) {
                return VALUE_EMPTY;
            } else {
                if (seconds.containsKey(second)) {
                    return seconds.get(second);
                } else {
                    return VALUE_EMPTY;
                }
            }
        }
    }

    public Set<Integer> containedSeconds() {
        if (seconds == null) {
            Set<Integer> zero = new HashSet<Integer>();
            zero.add(0);
            return zero;
        } else {
            return seconds.keySet();
        }
    }

    public boolean merge(MinuteValues values) {
        for (int second : values.containedSeconds()) {
            put(second, values.getValue(second));
        }
        if (values.zero != VALUE_EMPTY) {
            this.zero = values.zero;
        }
        return true;
    }

    public int getZero() {
        return zero;
    }

    public void setZero(int zero) {
        this.zero = zero;
    }

    public HashMap<Integer, Integer> getSeconds() {
        return seconds;
    }

    public void setSeconds(HashMap<Integer, Integer> seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "MinuteValues{" +
                "zero=" + zero +
                ", seconds=" + seconds +
                '}';
    }
}