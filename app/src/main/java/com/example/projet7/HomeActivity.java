package com.example.projet7;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.projet7.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
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
    }

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        setSupportActionBar(binding.toolbar);
                        NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment);
                        NavigationUI.setupWithNavController(binding.navigationBar, navController);
                        NavigationUI.setupWithNavController(binding.toolbar, navController);
                        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list, R.id.nav_workmates).build();
                        NavigationUI.setupActionBarWithNavController(HomeActivity.this, navController, appBarConfiguration);
                    } else {
                        Log.d("Permission", "permission denied");
                    }
                }
            }
    );
}