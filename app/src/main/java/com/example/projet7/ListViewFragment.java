package com.example.projet7;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentListViewBinding;

import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.restaurant.RecyclerViewInterface;
import com.example.projet7.ui.restaurant.RestaurantAdapter;
import com.example.projet7.ui.viewmodel.HomeViewModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListViewFragment extends Fragment implements RecyclerViewInterface {

    private FragmentListViewBinding binding;
    private HomeViewModel viewModel;

    private ArrayList<HashMap<String, Object>> mArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        FirebaseService firebaseService = FirebaseService.getInstance();
        mArrayList = new ArrayList<>();

        if (viewModel.getRestaurants() == null) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvRestaurant.setVisibility(View.GONE);
        } else {
            List<Restaurant> restaurants = viewModel.getRestaurants();
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvRestaurant.setVisibility(View.VISIBLE);

            for (Restaurant restaurant: restaurants) {
                HashMap<String, Object> hashMap = new HashMap<>();
                firebaseService.getUserNumberForRestaurant(restaurant.getFsq_id(), viewModel.getEmailUser(), new BaseFirebase() {
                    @Override
                    public void getSize(int size) {
                        super.getSize(size);
                        hashMap.put("size", size);
                        hashMap.put("name", restaurant.getName());
                        hashMap.put("detail", restaurant.getCategories().get(0).getName());
                        hashMap.put("address", restaurant.getLocation().getAddress());
                        hashMap.put("distance", restaurant.getDistance());
                        hashMap.put("img", viewModel.getImgRVByName(restaurant.getName()));
                        mArrayList.add(hashMap);
                        if (mArrayList.size() == restaurants.size()) {
                            binding.rvRestaurant.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.rvRestaurant.setAdapter(new RestaurantAdapter(requireContext(), restaurants, ListViewFragment.this::onItemClick, mArrayList));
                        }
                    }
                });
            }
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.nav_detail, viewModel.goToRestaurantById(true, position));
    }
}