package org.starfishrespect.myconsumption.server.business.sensors;

import sun.net.www.protocol.https.DefaultHostnameVerifier;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Tool to disable SSL check, used because of wrong Flukso certificate
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public final class SSLCheckUtil {
    /**
     * Prevent instantiation of utility class.
     */
    private SSLCheckUtil() {
    }

    /**
     * Trust manager that does not perform nay checks.
     */
    private static class NullX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    /**
     * Host name verifier that does not perform nay checks.
     */
    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Disable trust checks for SSL connections.
     */
    public static void disableChecks() {

        try {
            new URL("https://0.0.0.0/").getContent();
        } catch (IOException e) {
            // This invocation will always fail, but it will register the
            // default SSL provider to the URL class.
        }

        try {
            SSLContext sslc;

            sslc = SSLContext.getInstance("TLS");

            TrustManager[] trustManagerArray = {new NullX509TrustManager()};
            sslc.init(null, trustManagerArray, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void enableChecks() {
        HttpsURLConnection.setDefaultHostnameVerifier(new DefaultHostnameVerifier());
    }
}