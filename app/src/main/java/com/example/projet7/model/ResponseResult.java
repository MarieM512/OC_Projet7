package com.example.projet7.model;

import com.example.projet7.model.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseResult {

    @SerializedName("results")
    private List<Restaurant> results;

    public List<Restaurant> getRestaurants() {
        return results;
    }

    public void setRestaurants(List<Restaurant> results) {
        this.results = results;
    }
}
