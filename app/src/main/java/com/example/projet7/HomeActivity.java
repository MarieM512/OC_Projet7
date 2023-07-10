package com.example.projet7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.injection.ViewModelFactory;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private NavController mNavController;
    private ArrayList<String> restaurantName;
    private Boolean exist = false;

    private FirebaseService mFirebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        restaurantName = new ArrayList<>();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mNavController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navigationBar, mNavController);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list, R.id.nav_workmates).setOpenableLayout(binding.drawer).build();
        NavigationUI.setupWithNavController(binding.toolbar, mNavController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, mNavController);
        binding.navView.setNavigationItemSelectedListener(this);

        View headView = binding.navView.getHeaderView(0);
        ImageView image = headView.findViewById(R.id.image_nav);
        TextView name = headView.findViewById(R.id.tv_name_nav);
        TextView email = headView.findViewById(R.id.tv_email_nav);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(HomeViewModel.class);
        mFirebaseService = FirebaseService.getInstance();

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
            mFirebaseService.getChoiceDataByCurrentDate(viewModel, new BaseFirebase() {
                @Override
                public void getHashMapStringString(HashMap<String, String> hashMap) {
                    super.getHashMapStringString(hashMap);
                    if (!Objects.equals(hashMap.get(viewModel.getEmailUser()), "")) {
                        String id = hashMap.get(viewModel.getEmailUser());
                        viewModel.goToRestaurantById(mNavController, false, id);
                        binding.drawer.closeDrawer(GravityCompat.START);
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.message_no_lunch_selected), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (item.getItemId() == R.id.nav_settings) {
            mNavController.navigate(R.id.nav_parameter);
            binding.drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_logout) {
            viewModel.logout(getBaseContext(), this);
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
                    viewModel.goToRestaurantById(mNavController, false, viewModel.getLunchByName(queryString).get("id"));
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

        mFirebaseService.getChoiceDataByCurrentDate(viewModel, new BaseFirebase() {
            @Override
            public void getHashMapStringString(HashMap<String, String> hashMap) {
                super.getHashMapStringString(hashMap);
                for (String email: hashMap.keySet()) {
                    if (email.equals(viewModel.getEmailUser())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    mFirebaseService.createChoice(viewModel.getEmailUser());
                }
            }
        });
    }
}