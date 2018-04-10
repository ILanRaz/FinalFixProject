package com.example.ilan.myfinalproject.Extra;

import java.io.BufferedReader;
import java.io.InputStreamReader;


// handle json requests and response
public class Json {
    private static final int JSON_CONNECTION_TIMEOUT = 10000;


    public static String getJsonResult(String webURL) throws Exception {
        BufferedReader reader = null;
        String line, result = null;

        try {
            reader = new BufferedReader(new InputStreamReader(Http.getInputStream(webURL, JSON_CONNECTION_TIMEOUT)));

            result = "";

            while ((line = reader.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed get json from '" + webURL + "'");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result;
    }


}
