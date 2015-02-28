package org.starfishrespect.myconsumption.server.business.sensors;

import org.starfishrespect.myconsumption.server.business.sensors.exceptions.RetrieveException;

import java.util.Date;
import java.util.Random;

/**
 * Simple mock SensorRetriever inplementation
 */
public class MockRetriever implements SensorRetriever {

    public long allDataStart = 1262300400000L; // 1/1/2010 00h00
    public int maxValue = 1000;
    public int minValue = 0;
    public int interval = 60000; // 60 seconds

    @Override
    public SensorData getAllData() throws RetrieveException {
        return getDataSince(new Date(allDataStart));
    }

    @Override
    public SensorData getDataSince(Date startTime) throws RetrieveException {
        return getData(startTime, new Date());
    }

    @Override
    public SensorData getData(Date startTime, Date endTime) throws RetrieveException {
        SensorData data = new SensorData();
        Random random = new Random();
        int randomRange = maxValue - minValue;
        for (long i = startTime.getTime(); i < endTime.getTime(); i += interval) {
            data.addMeasurement((int) (i / 1000), minValue + random.nextInt(randomRange));
        }

        return data;
    }
}
