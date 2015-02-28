package biz.manex.sr.myconsumption.business.dao;

import biz.manex.sr.myconsumption.business.daoimpl.SensorDaoMongoImpl;
import biz.manex.sr.myconsumption.business.daoimpl.ValuesDaoMongoImpl;
import biz.manex.sr.myconsumption.business.datamodel.SensorDataset;
import biz.manex.sr.myconsumption.business.sensors.MockRetriever;
import biz.manex.sr.myconsumption.business.sensors.flukso.FluksoSensor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Patrick Herbeuval on 25/03/14.
 */
public class ValuesDaoMongoImplTest {

    private static final String username = "TEST_USER__";
    private static final String sensorName = "__FAKE_SENSOR__";
    private static final String sensorName2 = "__FAKE_SENSOR_2__";
    private ValuesDaoMongoImpl valueMongo;
    private SensorDaoMongoImpl sensorMongo;

    @Before
    public void setUp() throws Exception {

        AbstractApplicationContext ctx = new GenericXmlApplicationContext("biz/manex/sr/myconsumption/business/myconsumption-business.xml");

        sensorMongo = (SensorDaoMongoImpl) ctx.getBean("sensorDao");
        sensorMongo.insertSensor(new FluksoSensor(sensorName, "", ""));

        valueMongo = (ValuesDaoMongoImpl) ctx.getBean("valuesDao");
        valueMongo.init();
        valueMongo.reset();

        MockRetriever retriever = new MockRetriever();
        HashMap<Integer, HashMap<Integer, Integer>> sortedValues = new HashMap<Integer, HashMap<Integer, Integer>>();
        TreeMap<Integer, Integer> data = retriever.getDataSince(new Date(1390604400000L)).getData();

        for (int key : data.keySet()) {
            int hour = key - key % 3600;
            HashMap<Integer, Integer> hourData = sortedValues.get(hour);
            if (hourData == null) {
                hourData = new HashMap<Integer, Integer>();
                sortedValues.put(hour, hourData);
            }
            hourData.put(key % 3600, data.get(key));
        }

        for (int key : sortedValues.keySet()) {
            Date dateKey = new Date(key * 1000L);
            SensorDataset newValue = new SensorDataset(dateKey);
            newValue.addAllValues(sortedValues.get(key));
            valueMongo.setSensor(sensorName);
            valueMongo.insertOrUpdate(newValue);
            valueMongo.setSensor(sensorName2);
            valueMongo.insertOrUpdate(newValue);
        }
    }

    @Test
    public void testGetSensor() throws Exception {
        valueMongo.getSensor(new Date(0), new Date());
    }

    @Test
    public void testRemoveSensor() throws Exception {
        valueMongo.removeSensor(sensorName2);
    }
}