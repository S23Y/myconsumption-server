package org.starfishrespect.myconsumption.server.business.sensors;

import java.util.TreeMap;

/**
 * Intermediate class used to store sensor data when retrieving it.
 */
public class SensorData {

    private boolean mayHaveMoreData = true;

    private TreeMap<Integer, Integer> data;

    public SensorData() {
        data = new TreeMap<Integer, Integer>();
    }

    /**
     * Insert a measurement into the object
     */
    public void addMeasurement(int timestamp, int value) {
        data.put(timestamp, value);
    }

    public void append(SensorData otherData) {
        for (Integer key : otherData.data.keySet()) {
            data.put(key, otherData.data.get(key));
        }
        this.mayHaveMoreData = this.mayHaveMoreData && otherData.mayHaveMoreData;
    }

    public boolean mayHaveMoreData() {
        return mayHaveMoreData;
    }

    public void setMayHaveMoreData(boolean mayHaveMoreData) {
        this.mayHaveMoreData = mayHaveMoreData;
    }

    public int getFirstTimestamp() {
        if (data.size() == 0) {
            return -1;
        }
        return data.firstEntry().getKey();
    }

    public int getLastTimestamp() {
        if (data.size() == 0) {
            return -1;
        }
        return data.lastEntry().getKey();
    }

    public TreeMap<Integer, Integer> getData() {
        return data;
    }


    @Override
    public String toString() {
        return "SensorData{" +
                "mayHaveMoreData=" + mayHaveMoreData +
                ", data=" + data +
                '}';
    }
}