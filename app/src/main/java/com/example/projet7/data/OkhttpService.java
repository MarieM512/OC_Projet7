package com.example.projet7.data;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projet7.R;
import com.example.projet7.model.PlaceResult;
import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OkhttpService implements ApiInterface {
    private String jsonResponse = "";
    public String jsonImage = "";
    private Map<String, String> jsonResponseImgRV = new HashMap<>();
    private Map<String, String> jsonResponseImgDetail = new HashMap<>();
    private Double latitude;
    private Double longitude;
    private String id;
    private static volatile OkhttpService INSTANCE = null;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private GoogleMap mGoogleMap;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    private Activity mActivity;
    private RestaurantRepository mRestaurantRepository;

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
                        Log.d("API", "Failed to get a response from the request" + e.toString());
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

    public String getUrlImgRV(String name) {
        return jsonResponseImgRV.get(name);
    }

    public String getUrlImgDetail(String name) {
        return jsonResponseImgDetail.get(name);
    }

    @Override
    public void setParams(Double latitude, Double longitude, GoogleMap map) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mGoogleMap = map;
        getRestaurantData();
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
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
                        id = restaurant.getFsq_id();
                        LatLng pos = new LatLng(latitude, longitude);
                        mGoogleMap.addMarker(new MarkerOptions()
                                .title(name)
                                .position(pos));
                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                mRestaurantRepository = RestaurantRepository.getInstance();
                                String positionString = marker.getId().substring(1);
                                Integer position = Integer.parseInt(positionString);
                                NavController navController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", mRestaurantRepository.getId(position));
                                bundle.putString("name", mRestaurantRepository.getName(position));
                                bundle.putString("type", mRestaurantRepository.getType(position));
                                bundle.putString("address", mRestaurantRepository.getAddress(position));
                                bundle.putString("image", mRestaurantRepository.getImgDetail(mRestaurantRepository.getName(position)));
                                navController.navigate(R.id.action_nav_map_to_nav_detail, bundle);
                                return false;
                            }
                        });
                        getImgPlace(name, id);
                    }
                } else {
                    Log.d("Marker", "Failed to read response API");
                }
            }
        });
    }
}
