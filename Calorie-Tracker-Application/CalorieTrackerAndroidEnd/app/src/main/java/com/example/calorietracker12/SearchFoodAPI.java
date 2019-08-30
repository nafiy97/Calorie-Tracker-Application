package com.example.calorietracker12;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;



public class SearchFoodAPI {
    private static final String APP_ID= "73a68e5b";
    private static final String APP_KEY = "637c9a0667f07e5c39b47b6fee4a0a31";
    public static String search(String keyword) {
        keyword = keyword.replace(" ", "%20");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        try {
            url = new URL("https://api.edamam.com/api/food-database/parser?ingr="+keyword+"&app_id="+APP_ID+"&app_key="+APP_KEY);
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
