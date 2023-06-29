package com.example.projet7;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.databinding.FragmentListViewBinding;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.restaurant.RecyclerViewInterface;
import com.example.projet7.ui.restaurant.RestaurantAdapter;

import java.util.List;

public class ListViewFragment extends Fragment implements RecyclerViewInterface {

    private RestaurantRepository mRestaurantRepository = RestaurantRepository.getInstance();
    private FragmentListViewBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        if (mRestaurantRepository.mResponseResult == null) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvRestaurant.setVisibility(View.GONE);
        } else {
            List<Restaurant> restaurants = mRestaurantRepository.getRestaurants();
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvRestaurant.setVisibility(View.VISIBLE);
            binding.rvRestaurant.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvRestaurant.setAdapter(new RestaurantAdapter(getActivity().getApplicationContext(), restaurants, this));
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putString("id", mRestaurantRepository.getId(position));
        bundle.putString("name", mRestaurantRepository.getName(position));
        bundle.putString("type", mRestaurantRepository.getType(position));
        bundle.putString("address", mRestaurantRepository.getAddress(position));
        bundle.putString("image", mRestaurantRepository.getImgDetail(mRestaurantRepository.getName(position)));
        navController.navigate(R.id.action_nav_list_to_nav_detail, bundle);
    }
}