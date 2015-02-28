package org.starfishrespect.myconsumption.server.business.sensors.flukso;

import org.starfishrespect.myconsumption.server.business.SSLCheckUtil;
import org.starfishrespect.myconsumption.server.business.sensors.SensorData;
import org.starfishrespect.myconsumption.server.business.sensors.SensorRetriever;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.InvalidDataException;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.RequestException;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.RetrieveException;
import org.starfishrespect.myconsumption.server.business.sensors.exceptions.ServerException;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * SensorRetriever inplementation for the Flukso API
 */
public class FluksoRetriever implements SensorRetriever {

    private FluksoParams params;
    private FluksoSensor sensor;
    private RestTemplate restTemplate;

    /**
     * Creates the retriever and tries to get the sensors parameters from
     * the Flukso API
     *
     * @param sensor the wanted sensor
     * @throws RetrieveException if information cannot be retrieved from the
     *                           information API
     */

    public FluksoRetriever(FluksoSensor sensor) throws RetrieveException {
        this.sensor = sensor;

        restTemplate = new RestTemplate();

        SSLCheckUtil.disableChecks();

        this.params = retrieveParams();

        // SSLCheckUtil.enableChecks();

    }

    /**
     * Tries to retrieve all the data available for this Flukso sensor
     *
     * @return The retrieved data
     * @throws RetrieveException if any error occurs
     */
    @Override
    public SensorData getAllData() throws RetrieveException {
        return getDataSince(new Date(0));
    }

    /**
     * Tries to retrieve all data available from the sensor starting with a given
     * time
     *
     * @param startDate The time when we want to start to retrieve data. Must be positive and before current time
     * @return The retrieved data
     * @throws RetrieveException if any error occurs
     */
    @Override
    public SensorData getDataSince(Date startDate) throws RetrieveException {
        return getData(startDate, new Date());
    }

    /**
     * Tries to retrieve data available for the sensor in a given interval
     *
     * @param startTime Start of the interval
     * @param endTime   End of the interval. Must be higher of equal than startTime, and lower than actual time
     * @return the retrieved data
     * @throws RetrieveException if any error occurs
     */
    @Override
    public SensorData getData(Date startTime, Date endTime) throws RetrieveException {
        if (startTime.after(endTime)) {
            throw new RequestException(0, "Start Time must be lower than end time");
        }

        // cut data in day interval and retrieve with minute precision
        SensorData data = new SensorData();
        long now = Calendar.getInstance().getTimeInMillis();

        // last day
        Date currentComparisonDate = new Date(now - 86400000L);
        if (endTime.after(currentComparisonDate)) {
            if (startTime.after(currentComparisonDate)) {
                data.append(retrieve(startTime, endTime, maximumResolution(endTime)));
            } else {
                data.append(retrieve(currentComparisonDate, endTime, maximumResolution(endTime)));
            }
            endTime = currentComparisonDate;
        }

        if (endTime.before(startTime)) {
            return data;
        }
        // last week
        currentComparisonDate = new Date(now - 604800000L);
        if (endTime.after(currentComparisonDate)) {
            if (startTime.after(currentComparisonDate)) {
                data.append(retrieve(startTime, endTime, maximumResolution(endTime)));
            } else {
                data.append(retrieve(currentComparisonDate, endTime, maximumResolution(endTime)));
            }
            endTime = currentComparisonDate;
        }

        if (endTime.before(startTime)) {
            return data;
        }
        // last year
        currentComparisonDate = new Date(now - 31536000000L);
        if (endTime.after(currentComparisonDate)) {
            if (startTime.after(currentComparisonDate)) {
                data.append(retrieve(startTime, endTime, maximumResolution(endTime)));
            } else {
                data.append(retrieve(currentComparisonDate, endTime, maximumResolution(endTime)));
            }
            endTime = currentComparisonDate;
        }

        if (endTime.before(startTime)) {
            return data;
        }

        // retrieve annual values
        while (startTime.before(endTime)) {
            currentComparisonDate = new Date(endTime.getTime() - 31536000000L);
            if (startTime.after(currentComparisonDate)) {
                data.append(retrieve(startTime, endTime, maximumResolution(endTime)));
            } else {
                data.append(retrieve(currentComparisonDate, endTime, maximumResolution(endTime)));
            }
            endTime = currentComparisonDate;
            if (!data.mayHaveMoreData()) {
                break;
            }
        }
        return data;
    }

    /**
     * Tries to retrieve data for the sensor in the given interval and for the
     * given precision. All other retrieve methods use this
     *
     * @param start      start of the interval
     * @param end        end of the interval
     * @param resolution precision wanted.
     * @return the retrieved data
     * @throws RetrieveException if any error occurs
     */
    private SensorData retrieve(Date start, Date end, int resolution) throws RetrieveException {
        SensorData data = new SensorData();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-Version", "1.0");
        headers.set("X-Token", sensor.getToken());
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String url = "https://api.flukso.net/sensor/" + sensor.getFluksoId() + "?start=" + (start.getTime() / 1000)
                + "&end=" + (end.getTime() / 1000) + "&resolution=" + resolutionParam(resolution)
                + "&unit=watt";

        try {
            ArrayList<ArrayList> retrieved = restTemplate.exchange(url, HttpMethod.GET, entity, ArrayList.class).getBody();
            boolean valuesFound = false;
            for (ArrayList measurement : retrieved) {
                if (measurement.size() < 2) {
                    continue;
                }
                if (!(measurement.get(0) instanceof Integer) || !(measurement.get(1) instanceof Integer)) {
                    continue;
                }
                valuesFound = true;
                int timestamp = (Integer) measurement.get(0);
                int value = (Integer) measurement.get(1);
                // timestamp - resolution because timestamp is the end time
                data.addMeasurement(timestamp - resolution, value);
            }
            if (!valuesFound) {
                data.setMayHaveMoreData(false);
            }
        } catch (HttpClientErrorException httpException) {
            throw new RequestException(httpException.getStatusCode().value(), "Cannot retrieve data.");
        } catch (ResourceAccessException resourceException) {
            throw new RetrieveException("Resource exception");
        } catch (RestClientException restException) {
            throw new InvalidDataException("Non-valid data");
        }

        return data;
    }

    /**
     * Returns the maximum possible resolution available for the given time.
     *
     * @param startTime the time
     * @return the precision
     */
    private int maximumResolution(Date startTime) {
        long now = System.currentTimeMillis();
        long delay = now - startTime.getTime();
        delay /= 1000L;
        if (delay < 86400L) {
            return 60; // minute
        } else if (delay < 604800L) {
            return 900; // 15 min
        } else if (delay < 31536000L) {
            return 86400; // day
        } else {
            return 604800; // week
        }
    }

    /**
     * Returns the precision value string as needed for the API param
     *
     * @param resolution the int resolution
     * @return a string corresponding to the resolution, or null if wrong value
     */
    private String resolutionParam(int resolution) {
        switch (resolution) {
            case 60:
                return "minute;";
            case 900:
                return "15min;";
            case 3600:
                return "hour;";
            case 86400:
                return "day;";
            case 604800:
                return "week";
            default:
                return null;
        }
    }

    /**
     * Retrieves the parameters of the sensor from the API
     *
     * @return The parameters of this sensor
     * @throws RetrieveException if any error occurs
     */
    private FluksoParams retrieveParams() throws RetrieveException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-Version", "1.0");
        headers.set("X-Token", sensor.getToken());
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String url = "https://api.flukso.net/sensor/" + sensor.getFluksoId() + "?param=all";
        try {
            FluksoParams params = restTemplate.exchange(url, HttpMethod.GET, entity, FluksoParams.class).getBody();
        } catch (ResourceAccessException e) {
            throw new RetrieveException("Unknown retrieve exception");
        } catch (HttpClientErrorException httpError) {
            int errorCode = httpError.getStatusCode().value();
            switch (httpError.getStatusCode().value()) {
                case HttpStatus.SC_UNAUTHORIZED:
                case HttpStatus.SC_BAD_REQUEST:
                    throw new RequestException(errorCode, "Bad sensor id or token");
                case HttpStatus.SC_NOT_FOUND:
                    throw new RequestException(errorCode, "API not found");
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    throw new ServerException(errorCode, "Resource not found");
                default:
                    throw new RetrieveException("Unknown retrieve exception");
            }
        }
        return params;
    }
}
