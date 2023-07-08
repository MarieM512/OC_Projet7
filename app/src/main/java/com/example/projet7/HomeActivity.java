package com.example.projet7;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projet7.databinding.ActivityHomeBinding;
import com.example.projet7.injection.ViewModelFactory;
import com.example.projet7.model.Restaurant;
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private NavHostFragment mNavHostFragment;
    private NavController mNavController;
    private ArrayList<String> restaurantName;
    private Boolean exist = false;

    private FirebaseFirestore mFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        restaurantName = new ArrayList<>();

        mNavHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mNavController = mNavHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navigationBar, mNavController);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list, R.id.nav_workmates).setOpenableLayout(binding.drawer).build();
        NavigationUI.setupWithNavController(binding.toolbar, mNavController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, mNavController);
        binding.navView.setNavigationItemSelectedListener(this);

        View headView = binding.navView.getHeaderView(0);
        ImageView image = headView.findViewById(R.id.image_nav);
        TextView name = headView.findViewById(R.id.tv_name_nav);
        TextView email = headView.findViewById(R.id.tv_email_nav);
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(HomeViewModel.class);

        if (viewModel.getImgUser() == null) {
            image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(this).load(viewModel.getImgUser()).centerCrop().into(image);
        }
        name.setText(viewModel.getNameUser());
        email.setText(viewModel.getEmailUser());

        mNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getLabel() == getString(R.string.toolbar_title_workmates) || navDestination.getLabel() == getString(R.string.toolbar_title_parameter)) {
                    binding.toolbar.getMenu().findItem(R.id.research).setVisible(false);
                } else {
                    binding.toolbar.getMenu().findItem(R.id.research).setVisible(true);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_lunch) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String currentDate = sdf.format(new Date());
            mFirebaseFirestore.collection("choice").whereEqualTo("date", currentDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentChange dc: task.getResult().getDocumentChanges()) {
                            if (dc.getDocument().get("email").equals(viewModel.getEmailUser())) {
                                if (dc.getDocument().get("id").equals("")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.message_no_lunch_selected), Toast.LENGTH_SHORT).show();
                                } else {
                                    Bundle bundle = new Bundle();
                                    String id = dc.getDocument().get("id").toString();
                                    bundle.putString("id", id);
                                    bundle.putString("name", viewModel.getLunch(id).get("name"));
                                    bundle.putString("type", viewModel.getLunch(id).get("type"));
                                    bundle.putString("address", viewModel.getLunch(id).get("address"));
                                    bundle.putString("image", viewModel.getLunch(id).get("image"));
                                    mNavController.navigate(R.id.nav_detail, bundle);
                                    binding.drawer.closeDrawer(GravityCompat.START);
                                }
                            }
                        }
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.research);
        SearchView searchView = (SearchView) searchViewItem.getActionView();

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setThreshold(0);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantName.isEmpty()) {
                    for (Restaurant restaurant: viewModel.getRestaurants()) {
                        restaurantName.add(restaurant.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, restaurantName);
                    searchAutoComplete.setAdapter(adapter);
                }
            }
        });

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString = (String) parent.getItemAtPosition(position);
                searchAutoComplete.setText(queryString);
                if (mNavController.getCurrentDestination().getDisplayName().endsWith("nav_map")) {
                    viewModel.getMarker(queryString);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", viewModel.getId(position));
                    bundle.putString("name", viewModel.getName(position));
                    bundle.putString("type", viewModel.getType(position));
                    bundle.putString("address", viewModel.getAddress(position));
                    bundle.putString("image", viewModel.getImgDetail(position));
                    mNavController.navigate(R.id.action_nav_list_to_nav_detail, bundle);
                }
                searchView.setQuery("", false);
                searchView.setIconified(true);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());

        Map<String, Object> choice = new HashMap<>();
        choice.put("date", currentDate);
        choice.put("email", viewModel.getEmailUser());
        choice.put("id", "");
        mFirebaseFirestore.collection("choice")
                .whereEqualTo("date", currentDate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot dc: task.getResult().getDocuments()) {
                        if (Objects.equals(dc.get("email"), viewModel.getEmailUser())) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        mFirebaseFirestore.collection("choice").document().set(choice).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("TAG", "onComplete: New choice created");
                            }
                        });
                        exist = false;
                    }
                }
            }
        });
    }
}