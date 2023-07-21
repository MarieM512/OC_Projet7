package com.example.projet7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.data.ApiService;
import com.example.projet7.databinding.FragmentMapViewBinding;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.ResponseResult;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseService mFirebaseService = FirebaseService.getInstance();

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private GoogleMap map;
    private HomeViewModel viewModel;
    private FragmentMapViewBinding binding;
    private Marker mMarker;
    String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.myMap);
        supportMapFragment.getMapAsync(this);
        viewModel = new ViewModelProvider(getActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);

        binding.fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.getLastLocation()
                                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        viewModel.latitude = location.getLatitude();
                                        viewModel.longitude = location.getLongitude();
                                        focusCamera(map);
                                        setParameters();
                                        Log.d("Google Maps", "Location updated");
                                    }
                                });
                    }
                } else {
                    permissionLauncher.launch(permission);
                }
            }
        });
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        viewModel.mGoogleMap = googleMap;
        permissionLauncher.launch(permission);
    }

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
                        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                        if (viewModel.latitude == 0.00 && viewModel.longitude == 0.00) {
                                            viewModel.latitude = locationResult.getLastLocation().getLatitude();
                                            viewModel.longitude = locationResult.getLastLocation().getLongitude();
                                            focusCamera(map);
                                            setParameters();
                                        }
                                    }
                                }
                            };
                            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        } else {
                            Log.d("Permission", "permission denied");
                        }
                    }
                }
            }
    );

    private void setParameters() {
        viewModel.setParam(new ApiService() {
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
                                viewModel.getImgPlace(name, id);
                                addMarkers(pos, name, id);
                            }
                        }

                    }
                });
            }
        });
    }

    private void focusCamera(GoogleMap map) {
        LatLng pos = new LatLng(viewModel.latitude, viewModel.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
        map.setMinZoomPreference(15);
    }

    private void addMarkers(LatLng position, String name, String id) {

        mFirebaseService.getUserNumberForRestaurant(id, viewModel.getEmailUser(), new BaseFirebase() {
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
                if (!viewModel.mMarkerList.contains(mMarker)) {
                    viewModel.mMarkerList.add(mMarker);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String positionString = marker.getId().substring(1);
                Integer position = Integer.parseInt(positionString);
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_detail, viewModel.goToRestaurantById(true, position));
                return false;
            }
        });
    }
}