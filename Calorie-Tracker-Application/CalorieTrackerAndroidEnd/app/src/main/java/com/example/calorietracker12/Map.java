package com.example.calorietracker12;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.MapQuest;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Map extends Fragment {
    View vDisplayUnit;
    MapboxMap mMapboxMap;
    MapView mMapView;
//    LatLng LOCATION = new LatLng(37.7749, -122.4194);
    LatLng LOCATION;
    double latitude;
    double longitude;
    String home;

    ArrayList<String[]> places;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.map, container, false);
        mMapView = (MapView) vDisplayUnit.findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapView.setStreetMode();
            }
        });
        GetAddressAsyncTask getAddressAsyncTask = new GetAddressAsyncTask();
        getAddressAsyncTask.execute();
        return vDisplayUnit;
    }

    private void addMarker(MapboxMap mapboxMap,LatLng location,String title,String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        mapboxMap.addMarker(markerOptions);
        mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION, 14));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMapView !=null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView !=null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mMapView !=null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMapView !=null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            if (params[0] != null) {
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(params[0], 1);
                    String[] msg = new String[2];
                    if (addressList.size() > 0) {
                        msg[0] = String.valueOf(addressList.get(0).getLatitude());
                        msg[1] = String.valueOf(addressList.get(0).getLongitude());
                        return msg;
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] str) {
            if (str != null) {
                latitude = Double.valueOf(str[0]);
                longitude= Double.valueOf(str[1]);
                GetPlacesAsyncTask getPlacesAsyncTask = new GetPlacesAsyncTask();
                getPlacesAsyncTask.execute(str[0],str[1]);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LOCATION = new LatLng(latitude, longitude);
                        addMarker(mMapboxMap, LOCATION, home, "Welcome to " + home + "!");
                        if ((places != null)) {
                            for (String[] strings : places) {
//                                Icon icon =new Icon(url);
                                addMarker(mMapboxMap, new LatLng(Double.valueOf(strings[1]), Double.valueOf(strings[0])), strings[2], strings[3]);
                            }
                        }
                    }
                 },2000);
            }
        }
    }

    private class GetAddressAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            home = getAddressFromJson(RestClient.findFromUserId(Short.valueOf(Mainfragment.USERID)));
            return home;
        }

        @Override
        protected void onPostExecute(String str) {
            if ((str != null)) {
                SearchAsyncTask searchAsyncTask = new SearchAsyncTask();
                searchAsyncTask.execute(str);
            }
        }

        public String getAddressFromJson(String jsonLine) {
            String address = "";
            try {
                JSONObject jsonObject = new JSONObject(jsonLine);
                address = jsonObject.getString("userAddress");
            } catch (Exception e) {
            }
            return address;
        }
    }

    private class GetPlacesAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            places = getInformation(PlacesAPI.search(params[0],params[1]));

            return null;
        }

        public ArrayList<String[]> getInformation(String response){
            if((response!=null)&&(!response.equals("[]")))
            {
                ArrayList<String[]> data = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i =0;i<results.length();i++)
                    {
                        String[] values = new String[4];
                        JSONObject result = results.getJSONObject(i);
                        JSONObject place = result.getJSONObject("place");
                        JSONObject geometry = place.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        values[0] = String.valueOf(coordinates.getDouble(0));
                        values[1] = String.valueOf(coordinates.getDouble(1));

                        JSONObject properties = place.getJSONObject("properties");
                        values[2] = result.getString("name");
                        values[3]= properties.getString("street");
                        data.add(values);
                    }
                }catch (Exception e){
                }
                return data;
            }
            return null;
        }
    }
}