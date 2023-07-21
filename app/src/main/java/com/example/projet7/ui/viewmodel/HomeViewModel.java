package com.example.projet7.ui.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projet7.R;
import com.example.projet7.data.ApiService;
import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final RestaurantRepository mRestaurantRepository;
    private final FirebaseService mFirebaseService;
    private FirebaseUser mUser;
    private Double latitude = 0.00;
    private Double longitude = 0.00;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Marker> mMarkerList = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private static final String TAG = "Home ViewModel";

    public HomeViewModel(RestaurantRepository restaurantRepository, FirebaseService firebaseService, FirebaseUser firebaseUser) {
        this.mRestaurantRepository = restaurantRepository;
        this.mFirebaseService = firebaseService;
        mUser = firebaseUser;
        Log.d(TAG, "HomeViewModel: " );
    }

    /* Google Maps */

    public void getCurrentLocation(Context context, Activity activity, GoogleMap map) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateDistanceMeters(10)
                    .build();
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult.getLastLocation() == null) {
                        Log.d("Google Maps", "Location not updated");
                    } else {
                        Log.d("Google Maps", "Location updated");
                        if (latitude == 0.00 && longitude == 0.00) {
                            latitude = locationResult.getLastLocation().getLatitude();
                            longitude = locationResult.getLastLocation().getLongitude();
                            focusCamera(map);
                            mGoogleMap = map;
                            setParam(latitude, longitude, activity, map);
                        }
                    }
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    public void getLastLocation(Context context, Activity activity, GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            focusCamera(map);
                            setParam(latitude, longitude, activity, map);
                            Log.d("Google Maps", "Location updated");
                        }
                    });
        }
    }

    private void focusCamera(GoogleMap map) {
        LatLng pos = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
        map.setMinZoomPreference(15);
    }

    private void setParam(Double latitude, Double longitude, Activity activity, GoogleMap map) {
        mRestaurantRepository.setParameters(latitude, longitude, new ApiService() {
            @Override
            public void getRestaurantMarker(String jsonRestaurant) {
                mainHandler.post(new Runnable() {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    ResponseResult responseResult = gson.fromJson(jsonRestaurant, ResponseResult.class);
                    Double latitude;
                    Double longitude;
                    String name;
                    String id;

                    @Override
                    public void run() {
                        if (!jsonRestaurant.isEmpty()) {
                            for (Restaurant restaurant : responseResult.getRestaurants()) {
                                latitude = restaurant.getGeocodes().getMain().getLatitude();
                                longitude = restaurant.getGeocodes().getMain().getLongitude();
                                LatLng pos = new LatLng(latitude, longitude);
                                name = restaurant.getName();
                                id = restaurant.getFsq_id();
                                getImgPlace(name, id);
                                addMarkers(map, activity, pos, name, id);
                            }
                        }

                    }
                });
            }
        });
    }

    private void addMarkers(GoogleMap map, Activity activity, LatLng position, String name, String id) {

        mFirebaseService.getUserNumberForRestaurant(id, getEmailUser(), new BaseFirebase() {
            @Override
            public void getSize(int size) {
                super.getSize(size);
                if (size > 0) {
                    mMarker = map.addMarker(new MarkerOptions()
                            .title(name)
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                } else {
                    mMarker = map.addMarker(new MarkerOptions()
                            .title(name)
                            .position(position));
                }
                if (!mMarkerList.contains(mMarker)) {
                    mMarkerList.add(mMarker);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String positionString = marker.getId().substring(1);
                Integer position = Integer.parseInt(positionString);
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                goToRestaurantById(navController, true, position);
                return false;
            }
        });
    }

    private void getImgPlace(String name, String id) {
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

    public void logout(Context context, Activity activity) {
        AuthUI.getInstance().signOut(context)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activity.finish();
                    } else {
                        Log.d(TAG, String.valueOf(task.getException()));
                    }
                });
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

    public void goToRestaurantById(NavController navController, Boolean byPosition, Object idPosition) {
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
        navController.navigate(R.id.nav_detail, bundle);
    }
}
