package com.example.projet7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projet7.databinding.ActivityHomeBinding;
import com.firebase.ui.auth.AuthUI;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnLogOut.setOnClickListener(v -> {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            finish();
                        } else {
                            Toast.makeText(HomeActivity.this, "error logout", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}