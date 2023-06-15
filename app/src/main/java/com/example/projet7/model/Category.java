package com.example.projet7.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
