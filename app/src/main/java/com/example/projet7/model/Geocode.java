package com.example.projet7.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geocode {

    @SerializedName("main")
    private Position main;

    public Position getMain() {
        return main;
    }

    public void setMain(Position main) {
        this.main = main;
    }
}
