package com.example.projet7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projet7.databinding.ActivityHomeBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private NavController mNavController;
    private FirebaseUser user;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navigationBar, mNavController);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list, R.id.nav_workmates).setOpenableLayout(binding.drawer).build();
        NavigationUI.setupWithNavController(binding.toolbar, mNavController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, mNavController);
        binding.navView.setNavigationItemSelectedListener(this);

        View headView = binding.navView.getHeaderView(0);
        ImageView image = headView.findViewById(R.id.image_nav);
        TextView name = headView.findViewById(R.id.tv_name_nav);
        TextView email = headView.findViewById(R.id.tv_email_nav);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        if (user.getPhotoUrl() == null) {
            image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(this).load(user.getPhotoUrl()).centerCrop().into(image);
        }
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_lunch) {
            mFirebaseFirestore.collection("users").document(user.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getData().get("idChoice").equals("")) {
                            Toast.makeText(HomeActivity.this, getString(R.string.message_no_lunch_selected), Toast.LENGTH_SHORT).show();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", task.getResult().get("idChoice").toString());
                            bundle.putString("name", task.getResult().get("nameChoice").toString());
                            bundle.putString("type", task.getResult().get("typeChoice").toString());
                            bundle.putString("address", task.getResult().get("addressChoice").toString());
                            bundle.putString("image", task.getResult().get("imageChoice").toString());
                            mNavController.navigate(R.id.nav_detail, bundle);
                            binding.drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Log.d("TAG", "onComplete: " , task.getException());
                    }
                }
            });
        } else if (item.getItemId() == R.id.nav_settings) {
            mNavController.navigate(R.id.nav_parameter);
            binding.drawer.closeDrawer(GravityCompat.START);
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