package com.example.projet7.ui.viewmodel;

import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import com.example.projet7.data.ApiService;
import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.model.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final RestaurantRepository mRestaurantRepository;
    private FirebaseUser mUser;
    public Double latitude = 0.00;
    public Double longitude = 0.00;
    public List<Marker> mMarkerList = new ArrayList<>();
    public GoogleMap mGoogleMap;
    private static final String TAG = "Home ViewModel";

    public HomeViewModel(RestaurantRepository restaurantRepository, FirebaseUser firebaseUser) {
        this.mRestaurantRepository = restaurantRepository;
        mUser = firebaseUser;
    }

    /* Google Maps */

    public void setParam(ApiService apiService) {
        mRestaurantRepository.setParameters(latitude, longitude, apiService);
    }

    public void getImgPlace(String name, String id) {
        mRestaurantRepository.getImgPlace(name, id);
    }

    public void getMarker(String name) {
        for (Marker marker : mMarkerList) {
            if (Objects.equals(marker.getTitle(), name)) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
            }
        }
    }

    /* Firebase Auth */

    public String getNameUser() {
        return mUser.getDisplayName();
    }

    public String getEmailUser() {
        return mUser.getEmail();
    }

    public Uri getImgUser() {
        return mUser.getPhotoUrl();
    }

    /* API */

    public List<Restaurant> getRestaurants() {
        return mRestaurantRepository.getRestaurants();
    }

    public String getName(int position) {
        return mRestaurantRepository.getRestaurants().get(position).getName();
    }

    public String getType(int position) {
        return mRestaurantRepository.getRestaurants().get(position).getCategories().get(0).getName();
    }

    public String getAddress(int position) {
        return mRestaurantRepository.getRestaurants().get(position).getLocation().getAddress();
    }

    public String getDistance(int position) {
        return mRestaurantRepository.getRestaurants().get(position).getDistance() + "m";
    }

    public String getId(int position) {
        return mRestaurantRepository.getRestaurants().get(position).getFsq_id();
    }

    public String getImgDetail(int position) {
        return mRestaurantRepository.getImgDetail(getName(position));
    }

    public String getImgRV(int position) {
        return mRestaurantRepository.getImgRV(getName(position));
    }

    public String getImgRVByName(String name) {
        return mRestaurantRepository.getImgRV(name);
    }

    /* Refactor */

    public HashMap<String, String> getLunchById(String id) {
        HashMap<String, String> lunch = new HashMap<>();
        for (Restaurant restaurant : getRestaurants()) {
            if (restaurant.getFsq_id().equals(id)) {
                lunch.put("name", restaurant.getName());
                lunch.put("type", restaurant.getCategories().get(0).getName());
                lunch.put("address", restaurant.getLocation().getAddress());
                lunch.put("image", mRestaurantRepository.getImgDetail(restaurant.getName()));
                break;
            }
        }
        return lunch;
    }

    public HashMap<String, String> getLunchByName(String name) {
        HashMap<String, String> lunch = new HashMap<>();
        for (Restaurant restaurant : getRestaurants()) {
            if (restaurant.getName().equals(name)) {
                lunch.put("id", restaurant.getFsq_id());
                lunch.put("type", restaurant.getCategories().get(0).getName());
                lunch.put("address", restaurant.getLocation().getAddress());
                lunch.put("image", mRestaurantRepository.getImgDetail(restaurant.getName()));
                break;
            }
        }
        return lunch;
    }

    public Bundle goToRestaurantById(Boolean byPosition, Object idPosition) {
        Bundle bundle = new Bundle();
        if (byPosition) {
            int position = (Integer) idPosition;
            bundle.putString("id", getId(position));
            bundle.putString("name", getName(position));
            bundle.putString("type", getType(position));
            bundle.putString("address", getAddress(position));
            bundle.putString("image", getImgDetail(position));
        } else {
            String id = (String) idPosition;
            bundle.putString("id", id);
            bundle.putString("name", getLunchById(id).get("name"));
            bundle.putString("type", getLunchById(id).get("type"));
            bundle.putString("address", getLunchById(id).get("address"));
            bundle.putString("image", getLunchById(id).get("image"));
        }
        return bundle;
    }
}
