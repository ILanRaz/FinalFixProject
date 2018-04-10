package com.example.ilan.myfinalproject.Extra;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// handle the https requests
public class Http {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;


    private static HttpURLConnection getConnection(String webUrl, int connectionTimeout) throws IOException {
        URL url = new URL(webUrl);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(connectionTimeout);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Connection failed - " + connection.getResponseCode());
        }

        return connection;
    }


    public static InputStream getInputStream(String url, int connectionTimeout) throws IOException {
        return getConnection(url, connectionTimeout).getInputStream();
    }

    public static InputStream getInputStream(String url) throws IOException {
        return getInputStream(url, DEFAULT_CONNECTION_TIMEOUT);
    }

}
