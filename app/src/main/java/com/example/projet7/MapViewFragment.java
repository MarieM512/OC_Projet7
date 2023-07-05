package com.example.projet7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentMapViewBinding;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private GoogleMap map;
    private HomeViewModel viewModel;
    private FragmentMapViewBinding binding;
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
                    viewModel.getLastLocation(requireContext(), requireActivity(), map);
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
        permissionLauncher.launch(permission);
    }

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        viewModel.getCurrentLocation(requireContext(), requireActivity(), map);
                    } else {
                        Log.d("Permission", "permission denied");
                    }
                }
            }
    );
}