package com.chrissetiana.wunderapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CarQuery {

    public static List<CarActivity> fetchData(String source) {
        String data = null;
        URL url = buildUrl(source);

        try {
            assert url != null;
            data = buildHttp(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONData(data);
    }

    private static URL buildUrl(String source) {
        try {
            return new URL(source);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String buildHttp(URL url) throws IOException {
        String response = null;

        HttpURLConnection connection = null;
        InputStream stream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.connect();

            int code = connection.getResponseCode();

            if (code == 200) {
                stream = connection.getInputStream();
                response = readStream(stream);
            } else {
                Log.d("Query", "Error code: " + code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (stream != null) {
                stream.close();
            }
        }

        return response;
    }

    private static String readStream(InputStream stream) {
        try {
            StringBuilder builder = new StringBuilder();

            if (stream != null) {
                InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();

                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<CarActivity> getJSONData(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        List<CarActivity> cars = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(data);
            JSONArray placemarks = object.getJSONArray("placemarks");

            int len = placemarks.length();

            for (int i = 0; i < len; i++) {
                JSONObject property = placemarks.getJSONObject(i);
                String name = property.optString("name");
                String vin = property.optString("vin");
                String engine = property.optString("engineType");
                String fuel = property.optString("fuel");
                String exterior = property.optString("exterior");
                String interior = property.optString("interior");
                String address = property.optString("address");

                JSONArray coord = property.getJSONArray("coordinates");
                double lat = coord.optDouble(0);
                double lon = coord.optDouble(1);

                CarActivity car = new CarActivity(name, vin, engine, fuel, exterior, interior, address, lat, lon);
                cars.add(car);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return cars;
    }
}