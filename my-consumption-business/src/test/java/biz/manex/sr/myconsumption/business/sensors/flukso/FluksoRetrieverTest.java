package biz.manex.sr.myconsumption.business.sensors.flukso;

import biz.manex.sr.myconsumption.business.sensors.exceptions.RequestException;
import biz.manex.sr.myconsumption.business.sensors.exceptions.RetrieveException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

/**
 * Created by pat on 11/03/14.
 */

public class FluksoRetrieverTest {

    private FluksoRetriever retriever;
    private String testSensor = "9337af875dba89f84a362d44070a2ecf";
    private String testToken = "1d9bcb0d712c3c3266369dd6afdec14f";

    @Before
    public void setUp() throws Exception {
        retriever = new FluksoRetriever(
                new FluksoSensor("name", testSensor, testToken));
    }

    // Data retrieving tests
    @Test
    @Ignore
    public void getAllData() throws Exception {
        retriever.getAllData();
    }

    @Test
    public void getDataSince() throws Exception {
        retriever.getDataSince(new Date(System.currentTimeMillis() - 86400000L));
    }

    // errors tests
    @Test(expected = RetrieveException.class)
    public void wrongIntervalTest() throws Exception {
        retriever.getData(new Date(42), new Date(0));
    }

    @Test(expected = RetrieveException.class)
    public void negIntervalTest() throws Exception {
        retriever.getData(new Date(-42), new Date(42));
    }

    @Test
    public void wrongSensorTest() throws Exception {
        try {
            new FluksoRetriever(new FluksoSensor("this is", "a wrong", "sensor"));
        } catch (RequestException e) {
            Assert.assertEquals(400, e.getErrorCode());
        }
    }

}
