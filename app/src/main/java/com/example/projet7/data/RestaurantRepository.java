package com.example.projet7.data;

import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Objects;

public class RestaurantRepository {

    private final OkhttpService mOkhttpService;
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    public RestaurantRepository(OkhttpService okhttpService) {
        this.mOkhttpService = okhttpService;
    }

    public void setParameters(Double latitude, Double longitude, ApiService apiService) {
        mOkhttpService.setParams(latitude, longitude, apiService);
    }

    public List<Restaurant> getRestaurants() {
        return gson.fromJson(mOkhttpService.getResponseApi(), ResponseResult.class).getRestaurants();
    }

    public void getImgPlace(String name, String id) {
        mOkhttpService.getImgPlace(name, id);
    }

    public String getImgRV(String name) {
        String url = mOkhttpService.getUrlImgRV(name);
        if (Objects.equals(url, "invalid")) {
            return "https://play-lh.googleusercontent.com/YBChvJfwfwtGHAPiPYLn-c5jCMXS0p2CyT1TWrsFtjyrPn9foIMjLf62UuRUccwAwTI";
        } else {
            return url;
        }
    }

    public String getImgDetail(String name) {
        String url = mOkhttpService.getUrlImgDetail(name);
        if (Objects.equals(url, "invalid")) {
            return "https://play-lh.googleusercontent.com/YBChvJfwfwtGHAPiPYLn-c5jCMXS0p2CyT1TWrsFtjyrPn9foIMjLf62UuRUccwAwTI";
        } else {
            return url;
        }
    }
}
