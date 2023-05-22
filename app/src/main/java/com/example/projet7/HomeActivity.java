package com.example.projet7;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projet7.databinding.ActivityHomeBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        permissionLauncher.launch(permission);

//        binding.btnLogOut.setOnClickListener(v -> {
//            AuthUI.getInstance().signOut(this)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            finish();
//                        } else {
//                            Toast.makeText(HomeActivity.this, "error logout", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });

        binding.navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map_view) {
                replaceFragment(new MapViewFragment());
                binding.toolbar.setTitle(R.string.toolbar_title_view);
            } else if (item.getItemId() == R.id.list_view) {
                replaceFragment(new ListViewFragment());
                binding.toolbar.setTitle(R.string.toolbar_title_view);
            } else if (item.getItemId() == R.id.workmates) {
                replaceFragment(new WorkmatesFragment());
                binding.toolbar.setTitle(R.string.toolbar_title_workmates);
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        replaceFragment(new MapViewFragment());
                    } else {
                        Log.d("Permission", "permission denied");
                    }
                }
            }
    );
}