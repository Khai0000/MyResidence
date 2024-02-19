package com.example.googlemap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.grpc.android.BuildConfig;

public class HttpRequest {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<List<Map<String, Object>>> getEmergencyLocations(Context context,final String type, final double latitude, final double longitude) {
        return executor.submit(new Callable<List<Map<String, Object>>>() {
            public List<Map<String, Object>> call() {
                return makeNetworkRequest(context,type,latitude, longitude);
            }
        });
    }


    private static List<Map<String, Object>> makeNetworkRequest(Context context,String type, double latitude, double longitude) {
        List<Map<String, Object>> locationList = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=" + String.format("%.6f", latitude) + "," + String.format("%.6f", longitude) +
                    "&radius=10000" +
                    "&type=" + type +
                    "&key="+getApiKey(context));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            locationList = storeData(context,is);
        }
        catch (MalformedURLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return locationList;
    }

    private static List<Map<String, Object>> storeData(Context context,InputStream inputStream) {
        JsonParser parser = new JsonParser();
        JsonObject jsonResponse = parser.parse(new InputStreamReader(inputStream)).getAsJsonObject();
        List<Map<String, Object>> list = new ArrayList<>();

        if (jsonResponse.has("results")) {
            JsonArray results = jsonResponse.getAsJsonArray("results");

            for (int i = 0; i < results.size(); i++) {
                JsonObject place = results.get(i).getAsJsonObject();
                String name = place.get("name").getAsString();
                String address = place.get("vicinity").getAsString();
                double placeLatitude = place.getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble();
                double placeLongitude = place.getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble();

                String iconUrl = place.get("icon").getAsString();

                double rating = place.has("rating") ? place.get("rating").getAsDouble() : 0.0;
                int totalUserReviewed = place.has("user_ratings_total") ? place.get("user_ratings_total").getAsInt() : 0;

                String locationImage = null;

                JsonArray photosArray = place.getAsJsonArray("photos");
                if (photosArray != null && photosArray.size() > 0) {
                    JsonObject photoObject = photosArray.get(0).getAsJsonObject();
                    String photoReference = photoObject.get("photo_reference").getAsString();
                    locationImage = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key="+getApiKey(context);
                }

                String openNow = place.has("opening_hours") ? place.get("opening_hours").getAsJsonObject().get("open_now").getAsString() : null;
                Map<String, Object> placeData = new HashMap<>();
                placeData.put("name", name);
                placeData.put("latitude", placeLatitude);
                placeData.put("longitude", placeLongitude);
                placeData.put("icon", iconUrl);
                placeData.put("address", address);
                placeData.put("rating", rating);
                placeData.put("totalUserReview", totalUserReviewed);
                placeData.put("locationImage", locationImage);
                placeData.put("openNow", openNow);

                list.add(placeData);
            }
        } else {
            System.out.println("No results found.");
        }
        return list;
    }

    private static String getApiKey(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            return metaData.getString("MAPS_API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}