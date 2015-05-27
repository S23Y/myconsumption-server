package org.starfishrespect.myconsumption.server.business.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


/**
 * Contains all the values for one sensor, for an interval of
 * 1 hour
 */
public class SensorDataset implements Comparable<SensorDataset> {
    @Id
    private String id;
    @Indexed
    private Date timestamp;
    private TreeMap<Integer, MinuteValues> values;

    public SensorDataset(Date timestamp) {
        this.timestamp = timestamp;
        values = new TreeMap<Integer, MinuteValues>();
    }

    public SensorDataset setId(String id) {
        this.id = id;
        return this;
    }

    public SensorDataset addValue(int timestamp, int value) {
        int minute = (timestamp % 3600) / 60;

        MinuteValues minuteValue = values.get(minute);
        if (minuteValue == null) {
            minuteValue = new MinuteValues();
            values.put(minute, minuteValue);
        }
        minuteValue.put(timestamp % 60, value);
        return this;
    }

    public SensorDataset addAllValues(Map<Integer, Integer> newValues) {
        for (Integer key : newValues.keySet()) {
            addValue(key, newValues.get(key));
        }
        return this;
    }

    public TreeMap<Integer, MinuteValues> getValues() {
        return values;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SensorDataset{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", values=" + values +
                '}';
    }

    @Override
    public int compareTo(SensorDataset o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
