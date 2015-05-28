package org.starfishrespect.myconsumption.server.business.stats;

import org.starfishrespect.myconsumption.server.business.entities.DayStat;

import java.util.Comparator;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class DayStatComparator implements Comparator<DayStat> {
    @Override
    public int compare(DayStat o1, DayStat o2) {
        return o1.getDay().compareTo(o2.getDay());
    }
}
