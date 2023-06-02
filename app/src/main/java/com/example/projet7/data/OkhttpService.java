package com.example.projet7.data;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkhttpService implements ApiInterface {
    private String jsonResponse = "";
    private Double latitude;
    private Double longitude;
    private static volatile OkhttpService INSTANCE = null;

    OkHttpClient client = new OkHttpClient();
    Thread requestThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url("https://api.foursquare.com/v3/places/search?ll=" + latitude.toString() + "%2C" + longitude.toString() + "&radius=500&categories=13065")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "fsq3Zhjv1D+uQNmBW2EDwVsaFLeA62RM61Heqhfru1XLYTo=")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                jsonResponse = response.body().string();
                Log.d("api", jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public static OkhttpService getInstance() {
        if(INSTANCE == null) {
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
    public void getLatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        requestThread.start();
    }
}
