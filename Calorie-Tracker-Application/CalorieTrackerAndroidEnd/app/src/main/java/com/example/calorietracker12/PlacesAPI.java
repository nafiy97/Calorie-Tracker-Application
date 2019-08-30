package com.example.calorietracker12;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PlacesAPI {

    private static final String SORT= "distance";
    private static final String KEY = "Z1fjCeGbPaPTg5L10DQGEyOYWaY8jqcZ";
    private static final String QUERY= "park";
    private static final String FEEDBACK= "true";
    private static final String RADIUS = "5000";

    public static String search(String latitude,String longitude) {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        try {
            url = new URL("https://www.mapquestapi.com/search/v4/place?sort="+SORT+"&feedback="+FEEDBACK+"&key="+KEY+"&circle="+longitude+"%2C"+latitude+"%2C"+RADIUS+"&q="+QUERY);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            connection.disconnect();
        }
        return textResult;
    }

}

