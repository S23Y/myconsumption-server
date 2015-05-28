package org.starfishrespect.myconsumption.server.business.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starfishrespect.myconsumption.server.business.exceptions.InvalidRequestException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.GCM_SEND_ENDPOINT;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_COLLAPSE_KEY;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_DELAY_WHILE_IDLE;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_DRY_RUN;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_PAYLOAD_PREFIX;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_REGISTRATION_ID;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_RESTRICTED_PACKAGE_NAME;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.PARAM_TIME_TO_LIVE;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.TOKEN_CANONICAL_REG_ID;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.TOKEN_ERROR;
import static org.starfishrespect.myconsumption.server.business.notifications.NotificationConstants.TOKEN_MESSAGE_ID;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Adapted by Thibaud Ledent from
 * https://github.com/google/gcm/blob/master/gcm-server/src/com/google/android/gcm/server/Sender.java
 */
public class NotificationSender {
    
    protected static final String UTF8 = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    private final String key;

    /**
     * Default constructor.
     * @param key API key obtained through the Google API Console.
     */
    public NotificationSender(String key) {
        this.key = nonNull(key);
    }

    /**
     * Sends a notification message without retrying in case of service unavailability.
     *
     * @throws IllegalArgumentException if registrationId is {@literal null}.
     */
    public boolean sendNoRetry(NotificationMessage message, String registrationId)
            throws IOException {
        StringBuilder body = newBody(PARAM_REGISTRATION_ID, registrationId);
        Boolean delayWhileIdle = message.isDelayWhileIdle();
        if (delayWhileIdle != null) {
            addParameter(body, PARAM_DELAY_WHILE_IDLE, delayWhileIdle ? "1" : "0");
        }
        Boolean dryRun = message.isDryRun();
        if (dryRun != null) {
            addParameter(body, PARAM_DRY_RUN, dryRun ? "1" : "0");
        }
        String collapseKey = message.getCollapseKey();
        if (collapseKey != null) {
            addParameter(body, PARAM_COLLAPSE_KEY, collapseKey);
        }
        String restrictedPackageName = message.getRestrictedPackageName();
        if (restrictedPackageName != null) {
            addParameter(body, PARAM_RESTRICTED_PACKAGE_NAME, restrictedPackageName);
        }
        Integer timeToLive = message.getTimeToLive();
        if (timeToLive != null) {
            addParameter(body, PARAM_TIME_TO_LIVE, Integer.toString(timeToLive));
        }
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null) {
                logger.warn("Ignoring payload entry thas has null: " + entry);
            } else {
                key = PARAM_PAYLOAD_PREFIX + key;
                addParameter(body, key, URLEncoder.encode(value, UTF8));
            }
        }
        String requestBody = body.toString();
        logger.info("Request body: " + requestBody);
        HttpURLConnection conn;
        int status;
        try {
            conn = post(GCM_SEND_ENDPOINT, requestBody);
            status = conn.getResponseCode();
        } catch (IOException e) {
            logger.error("IOException posting to GCM", e);
            return false;
        }
        if (status / 100 == 5) {
            logger.error("GCM service is unavailable (status " + status + ")");
            return false;
        }
        String responseBody;
        if (status != 200) {
            try {
                responseBody = getAndClose(conn.getErrorStream());
                logger.info("Plain post error response: " + responseBody);
            } catch (IOException e) {
                // ignore the exception since it will thrown an InvalidRequestException
                // anyways
                responseBody = "N/A";
                logger.error("Exception reading response: ", e);
            }
            throw new InvalidRequestException(status, responseBody);
        } else {
            try {
                responseBody = getAndClose(conn.getInputStream());
            } catch (IOException e) {
                logger.error("Exception reading response: ", e);
                // return null so it can retry
                return false;
            }
        }
        String[] lines = responseBody.split("\n");
        if (lines.length == 0 || lines[0].equals("")) {
            throw new IOException("Received empty response from GCM service.");
        }
        String firstLine = lines[0];
        String[] responseParts = split(firstLine);
        String token = responseParts[0];
        String value = responseParts[1];

        switch (token) {
            case TOKEN_MESSAGE_ID:
                //Builder builder = new Result.Builder().messageId(value);
                // check for canonical registration id
                if (lines.length > 1) {
                    String secondLine = lines[1];
                    responseParts = split(secondLine);
                    token = responseParts[0];
                    value = responseParts[1];
                    if (token.equals(TOKEN_CANONICAL_REG_ID)) {
                        //builder.canonicalRegistrationId(value);
                    } else {
                        logger.warn("Invalid response from GCM: " + responseBody);
                    }
                }
                logger.info("Message created succesfully");
                return true;
            case TOKEN_ERROR:
                return false;
            default:
                throw new IOException("Invalid response from GCM: " + responseBody);
        }
    }

    private String[] split(String line) throws IOException {
        String[] split = line.split("=", 2);
        if (split.length != 2) {
            throw new IOException("Received invalid response line from GCM: " + line);
        }
        return split;
    }

    /**
     * Make an HTTP post to a given URL.
     *
     * @return HTTP response.
     */
    protected HttpURLConnection post(String url, String body)
            throws IOException {
        return post(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
    }

    /**
     * Makes an HTTP POST request to a given endpoint.
     *
     * <p>
     * <strong>Note: </strong> the returned connected should not be disconnected,
     * otherwise it would kill persistent connections made using Keep-Alive.
     *
     * @param url endpoint to post the request.
     * @param contentType type of request.
     * @param body body of the request.
     *
     * @return the underlying connection.
     *
     * @throws IOException propagated from underlying methods.
     */
    protected HttpURLConnection post(String url, String contentType, String body)
            throws IOException {
        if (url == null || body == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        if (!url.startsWith("https://")) {
            logger.warn("URL does not use https: " + url);
        }
        logger.info("Sending POST to " + url);
        logger.info("POST body: " + body);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = getConnection(url);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Authorization", "key=" + key);
        OutputStream out = conn.getOutputStream();
        try {
            out.write(bytes);
        } finally {
            close(out);
        }
        return conn;
    }

    /**
     * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
     *
     * @param name initial parameter for the POST.
     * @param value initial value for that parameter.
     * @return StringBuilder to be used an HTTP POST body.
     */
    protected static StringBuilder newBody(String name, String value) {
        return new StringBuilder(nonNull(name)).append('=').append(nonNull(value));
    }

    /**
     * Adds a new parameter to the HTTP POST body.
     *
     * @param body HTTP POST body.
     * @param name parameter's name.
     * @param value parameter's value.
     */
    protected static void addParameter(StringBuilder body, String name,
                                       String value) {
        nonNull(body).append('&')
                .append(nonNull(name)).append('=').append(nonNull(value));
    }

    static <T> T nonNull(T argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument cannot be null");
        }
        return argument;
    }

    /**
     * Convenience method to convert an InputStream to a String.
     * <p>
     * If the stream ends in a newline character, it will be stripped.
     * <p>
     * If the stream is {@literal null}, returns an empty string.
     */
    protected static String getString(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream));
        StringBuilder content = new StringBuilder();
        String newLine;
        do {
            newLine = reader.readLine();
            if (newLine != null) {
                content.append(newLine).append('\n');
            }
        } while (newLine != null);
        if (content.length() > 0) {
            // strip last newline
            content.setLength(content.length() - 1);
        }
        return content.toString();
    }

    private static String getAndClose(InputStream stream) throws IOException {
        try {
            return getString(stream);
        } finally {
            if (stream != null) {
                close(stream);
            }
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore error
                logger.error("IOException closing stream", e);
            }
        }
    }

    /**
     * Gets an {@link HttpURLConnection} given an URL.
     */
    protected HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        return conn;
    }

}
