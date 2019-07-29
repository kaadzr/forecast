package com.globus_ltd.forecast_sber_test.transport;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    private static final String GET = "GET";
    private int connectionTimeout;

    public HttpClient(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @WorkerThread
    public String executeGetRequest(URL url) throws IOException {
        String response;
        HttpURLConnection connection = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder readStringBuilder = new StringBuilder();

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(connectionTimeout);

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                // Input stream from url connection
                InputStream inputStream = connection.getInputStream();

                inputStreamReader = new InputStreamReader(inputStream);

                bufferedReader = new BufferedReader(inputStreamReader);

                // Reading server response
                String line = bufferedReader.readLine();

                while (line != null) {
                    readStringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                response = readStringBuilder.toString();

                return response;
            } else {
                throw new HttpFailedException(statusCode);
            }
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
