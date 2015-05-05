package org.starfishrespect.myconsumption.server.stats;

import org.starfishrespect.myconsumption.server.entities.DayStat;

import java.util.Comparator;

/**
 * Created by thibaud on 05.05.15.
 */
public class DayStatComparator implements Comparator<DayStat> {
    @Override
    public int compare(DayStat o1, DayStat o2) {
        return o1.getDay().compareTo(o2.getDay());
    }
}
