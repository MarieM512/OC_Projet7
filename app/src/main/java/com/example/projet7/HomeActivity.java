package com.example.projet7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.projet7.databinding.ActivityHomeBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navigationBar, navController);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list, R.id.nav_workmates).setOpenableLayout(binding.drawer).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_launch) {
            Log.d("item", "onNavigationItemSelected: Nav launch");
        } else if (item.getItemId() == R.id.nav_settings) {
            Log.d("item", "onNavigationItemSelected: Nav settings");
        } else if (item.getItemId() == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            finish();
                        } else {
                            Toast.makeText(this, "Error logout", Toast.LENGTH_SHORT).show();
                            Log.d("Logout", String.valueOf(task.getException()));
                        }
                    });
        }
        return true;
    }
}