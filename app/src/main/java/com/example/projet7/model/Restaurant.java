package com.example.projet7.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {

    @SerializedName("categories")
    private List<Category> categories;
    @SerializedName("distance")
    private int distance;
    @SerializedName("geocodes")
    private Geocode geocodes;
    @SerializedName("location")
    private Location location;
    @SerializedName("name")
    private String name;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Geocode getGeocodes() {
        return geocodes;
    }

    public void setGeocodes(Geocode geocodes) {
        this.geocodes = geocodes;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
