package com.example.projet7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projet7.databinding.ActivityHomeBinding;
import com.firebase.ui.auth.AuthUI;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    enum NavFrag { MapView, ListView, Workmates }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        replaceFragment(new MapViewFragment());

        setSupportActionBar(binding.toolbar);

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

}