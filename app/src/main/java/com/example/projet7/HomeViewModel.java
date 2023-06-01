package com.example.projet7;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HomeViewModel extends ViewModel {

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Double latitude;
    private Double longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String jsonResponse;

    void getCurrentLocation(Context context, Activity activity, GoogleMap map) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                LatLng pos = new LatLng(latitude, longitude);
                                map.moveCamera(CameraUpdateFactory.newLatLng(pos));
                                map.setMinZoomPreference(15);
                                try {
                                    fetchRestaurant();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Toast.makeText(context, "Failed to location, please click on location button", Toast.LENGTH_SHORT).show();
                                updateLocation(context);
                            }
                        }
                    });
        }
    }

    private void updateLocation(Context context) {
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    Log.d("location", "Location not updated");
                } else {
                    Log.d("location", "location updated");
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
        }
    }

    private void fetchRestaurant() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://api.foursquare.com/v3/places/search?ll=" + latitude.toString() + "%2C" + longitude.toString() + "&radius=500&categories=13065")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "fsq3Zhjv1D+uQNmBW2EDwVsaFLeA62RM61Heqhfru1XLYTo=")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    jsonResponse = response.body().string();
                    Log.d("api", jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        requestThread.start();
    }
}
