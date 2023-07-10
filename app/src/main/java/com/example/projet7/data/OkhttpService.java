package com.example.projet7.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.projet7.model.PlaceResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OkhttpService implements ImgService {


    public String jsonRestaurant = "";
    public String jsonImage = "";
    private Map<String, String> jsonResponseImgRV = new HashMap<>();
    private Map<String, String> jsonResponseImgDetail = new HashMap<>();
    private Double latitude;
    private Double longitude;
    private static volatile OkhttpService INSTANCE = null;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

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

    private void getRestaurantData(ApiService apiService) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.foursquare.com/v3/places/search?ll=" + latitude.toString() + "%2C" + longitude.toString() + "&radius=500&categories=13065")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "fsq3Zhjv1D+uQNmBW2EDwVsaFLeA62RM61Heqhfru1XLYTo=")
                .build();
        mainHandler.post(() -> client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request1, IOException e) {
                Log.d("Restaurant API", "Failed to get a response from the request" + e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                jsonRestaurant = response.body().string();
                Log.d("Restaurant API", jsonRestaurant);
                apiService.getRestaurantMarker(jsonRestaurant);
            }
        }));
    }

    public void getImgPlace(String name, String id) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.foursquare.com/v3/places/" + id + "/photos?classifications=food")
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
                        Log.d("API_IMG", "Failed to get a response img from the request");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        jsonImage = response.body().string();
                        if (jsonImage.startsWith("[{")){
                            PlaceResult[] mPlaceResult = gson.fromJson(jsonImage, PlaceResult[].class);
                            String urlRV = mPlaceResult[0].getPrefix() + "64x64" + mPlaceResult[0].getSuffix();
                            String urlDetail = mPlaceResult[0].getPrefix() + "410x300" + mPlaceResult[0].getSuffix();
                            jsonResponseImgRV.put(name, urlRV);
                            jsonResponseImgDetail.put(name, urlDetail);
                        } else {
                            jsonResponseImgRV.put(name, "invalid");
                            jsonResponseImgDetail.put(name, "invalid");
                        }
                    }
                });
            }
        });
    }

    public String getResponseApi() {
        return jsonRestaurant;
    }

    public String getUrlImgRV(String name) {
        return jsonResponseImgRV.get(name);
    }

    public String getUrlImgDetail(String name) {
        return jsonResponseImgDetail.get(name);
    }

    public void setParams(Double latitude, Double longitude, ApiService apiService) {
        this.latitude = latitude;
        this.longitude = longitude;
        getRestaurantData(apiService);
    }
}
