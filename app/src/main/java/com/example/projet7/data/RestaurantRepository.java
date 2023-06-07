package com.example.projet7.data;

import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class RestaurantRepository {

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    OkhttpService mOkhttpService = OkhttpService.getInstance();
    private static volatile RestaurantRepository INSTANCE = null;

    ResponseResult mResponseResult = gson.fromJson(mOkhttpService.getResponseApi(), ResponseResult.class);

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

    public String getImg(int position) {
        return mResponseResult.getRestaurants().get(position).getCategories().get(0).getIcon().getPrefix() + "88.png";
    }

}
