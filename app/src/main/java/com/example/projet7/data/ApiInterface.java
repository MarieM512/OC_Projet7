package com.example.projet7.data;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;

public interface ApiInterface {
    void setParams(Double latitude, Double longitude, GoogleMap map);
}
