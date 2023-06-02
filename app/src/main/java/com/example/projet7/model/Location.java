package com.example.projet7.model;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("address")
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
