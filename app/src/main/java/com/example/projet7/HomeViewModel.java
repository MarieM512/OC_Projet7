package com.example.projet7;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import com.example.projet7.data.OkhttpService;
import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HomeViewModel extends ViewModel {

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Double latitude = 0.00;
    private Double longitude = 0.00;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private OkhttpService mOkhttpService = OkhttpService.getInstance();

    void getCurrentLocation(Context context, GoogleMap map) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(2000)
                    .setMaxUpdateDelayMillis(100)
                    .build();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult.getLastLocation() == null) {
                        Log.d("location", "Location not updated");
                    } else {
                        Log.d("location", "Location updated");
                        if (latitude == 0.00 && longitude == 0.00) {
                            latitude = locationResult.getLastLocation().getLatitude();
                            longitude = locationResult.getLastLocation().getLongitude();
                            LatLng pos = new LatLng(latitude, longitude);
                            map.moveCamera(CameraUpdateFactory.newLatLng(pos));
                            map.setMinZoomPreference(15);
                            mOkhttpService.setParams(latitude, longitude, map);
                        }
                    }
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
        }
    }

    void getLastLocation(Context context, Activity activity, GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng pos = new LatLng(latitude, longitude);
                            map.moveCamera(CameraUpdateFactory.newLatLng(pos));
                            map.setMinZoomPreference(15);
                            mOkhttpService.setParams(latitude, longitude, map);
                            Log.d("location", "Location updated");
                        }
                    });
        }
    }
}
