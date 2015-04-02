package org.starfishrespect.myconsumption.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.starfishrespect.myconsumption.server.business.sensors.SensorRetriever;
import org.starfishrespect.myconsumption.server.business.sensors.flukso.FluksoRetriever;
import org.starfishrespect.myconsumption.server.business.sensors.flukso.FluksoSensor;
import org.starfishrespect.myconsumption.server.entities.Sensor;
import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.exceptions.DaoException;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.RetrieveException;
import org.starfishrespect.myconsumption.server.repositories.SensorRepository;
import org.starfishrespect.myconsumption.server.repositories.ValuesRepository;

import java.util.*;

/**
 * Tool that perform a single retrieve operation, for one or all sensors
 */
public class SensorsDataRetriever {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private ValuesRepository valuesRepository;

    public SensorsDataRetriever(SensorRepository sRepo, ValuesRepository vRepo) {
        this.sensorRepository = sRepo;
        this.valuesRepository = vRepo;
    }

    /**
     * Retrieve all data for all sensors present in database
     *
     * @return false if any error happened
     */
    public boolean retrieveAll() {
        List<Sensor> list = sensorRepository.getAllSensors();
        return retrieve(sensorRepository.getAllSensors(), null);
    }

    /**
     * Retrieves and stores the data for one user
     *
     * @param onlyThisSensorId retrieve only data for one sensor with this id
     * @return
     */
    public boolean retrieve(List<Sensor> sensors, String onlyThisSensorId) {
        boolean allSuccessful = true;
        for (Sensor sensor : sensors) {
            System.out.println("Retrieve data for sensor " + sensor.getId());
            try {
                valuesRepository.setSensor(sensor.getId());
                valuesRepository.init();
                if (onlyThisSensorId != null) {
                    if (!sensor.getId().equals(onlyThisSensorId)) {
                        continue;
                    }
                }
                HashMap<Integer, HashMap<Integer, Integer>> sortedValues = new HashMap<Integer, HashMap<Integer, Integer>>();
                Date lastValue = sensor.getLastValue();
                SensorRetriever retriever = null;
                if (sensor instanceof FluksoSensor) {
                    retriever = new FluksoRetriever((FluksoSensor) sensor);
                }
                if (retriever == null) {
                    System.out.println("This sensor type has not been found !");
                    continue;
                }
                TreeMap<Integer, Integer> data = retriever.getDataSince(lastValue).getData();
                if (data.size() != 0) {
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
                        valuesRepository.insertOrUpdate(newValue);
                    }
                    if (sensor.getLastValue().before(new Date(data.lastKey() * 1000L))) {
                        sensor.setLastValue(new Date(data.lastKey() * 1000L));
                    }
                    if (sensor.getFirstValue().after(new Date(data.firstKey() * 1000L)) || sensor.getFirstValue().getTime() == 0) {
                        sensor.setFirstValue(new Date(data.firstKey() * 1000L));
                    }
                    // sync operation, this avoid to insert a sensor who would have been deleted
                    // while retrieving its data
                    int currentUsageCount = sensorRepository.getUsageCount(sensor.getId());
                    if (currentUsageCount > -1) {
                        // update, the field may have been incremented during retrieving
                        sensor.setUsageCount(currentUsageCount);
                        sensor.setDead(false);
                        sensorRepository.updateSensor(sensor);
                    }
                    System.out.println("retrieve successful");
                } else {
                    System.out.println("No values retrieved for this sensor");
                    if (!sensor.isDead()) {
                        // test if sensor is dead ?
                        Calendar cal = new GregorianCalendar();
                        cal.add(Calendar.HOUR, -6);
                        if (sensor.getLastValue().before(new Date(cal.getTimeInMillis()))) {
                            System.out.println("So sign of live in the last 6 hours ! Set status as dead");
                            sensor.setDead(true);
                            sensorRepository.updateSensor(sensor);
                        }
                    } else {
                        System.out.println("Sensor is still dead");
                    }
                }
            } catch (RetrieveException | DaoException e) {
                System.err.println(e.getMessage());
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }
}