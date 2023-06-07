package com.example.projet7.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.projet7.HomeActivity;
import com.example.projet7.HomeViewModel;
import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkhttpService implements ApiInterface {
    private String jsonResponse = "";
    private Double latitude;
    private Double longitude;
    private static volatile OkhttpService INSTANCE = null;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private GoogleMap mGoogleMap;

    private void getRestaurantData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.foursquare.com/v3/places/search?ll=" + latitude.toString() + "%2C" + longitude.toString() + "&radius=500&categories=13065")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "fsq3Zhjv1D+uQNmBW2EDwVsaFLeA62RM61Heqhfru1XLYTo=")
                .build();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d("API", "Failed to get a response from the request");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        jsonResponse = response.body().string();
                        Log.d("API", jsonResponse);
                        getRestaurantMarker();
                    }
                });
            }
        });
    }

    public static OkhttpService getInstance() {
        if (INSTANCE == null) {
            synchronized (OkhttpService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OkhttpService();
                }
            }
        }
        return INSTANCE;
    }

    public String getResponseApi() {
        return jsonResponse;
    }

    @Override
    public void setParams(Double latitude, Double longitude, GoogleMap map) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mGoogleMap = map;
        getRestaurantData();
    }

    private void getRestaurantMarker() {
        mainHandler.post(new Runnable() {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ResponseResult responseResult = gson.fromJson(jsonResponse, ResponseResult.class);
            Double latitude;
            Double longitude;
            String name;
            @Override
            public void run() {
                if (!jsonResponse.isEmpty()) {
                    for (Restaurant restaurant : responseResult.getRestaurants()) {
                        latitude = restaurant.getGeocodes().getMain().getLatitude();
                        longitude = restaurant.getGeocodes().getMain().getLongitude();
                        name = restaurant.getName();
                        LatLng pos = new LatLng(latitude, longitude);
                        mGoogleMap.addMarker(new MarkerOptions()
                                .title(name)
                                .position(pos));
                    }
                } else {
                    Log.d("Marker", "Failed to read response API");
                }
            }
        });
    }
}
