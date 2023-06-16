package com.example.projet7.data;

import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Objects;

public class RestaurantRepository {

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    OkhttpService mOkhttpService = OkhttpService.getInstance();
    private static volatile RestaurantRepository INSTANCE = null;

    public ResponseResult mResponseResult = gson.fromJson(mOkhttpService.getResponseApi(), ResponseResult.class);

    public static RestaurantRepository getInstance() {
        if(INSTANCE == null) {
            synchronized (OkhttpService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RestaurantRepository();
                }
            }
        }
        return INSTANCE;
    }

    public List<Restaurant> getRestaurants() {
        return mResponseResult.getRestaurants();
    }

    public String getName(int position) {
        return mResponseResult.getRestaurants().get(position).getName();
    }

    public String getType(int position) {
        return mResponseResult.getRestaurants().get(position).getCategories().get(0).getName();
    }

    public String getAddress(int position) {
        return mResponseResult.getRestaurants().get(position).getLocation().getAddress();
    }

    public String getDistance(int position) {
        return mResponseResult.getRestaurants().get(position).getDistance() + "m";
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
