package com.example.projet7;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.model.Restaurant;

import java.util.List;

public class ListViewFragment extends Fragment implements RecyclerViewInterface {

    private RestaurantRepository mRestaurantRepository = RestaurantRepository.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvRestaurant);

        List<Restaurant> restaurants = mRestaurantRepository.getRestaurants();


        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new RestaurantAdapter(getActivity().getApplicationContext(), restaurants, this));
        return view;
    }

    @Override
    public void onItemClick(int position) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle bundle = new Bundle();
        bundle.putString("name", mRestaurantRepository.getName(position));
        bundle.putString("type", mRestaurantRepository.getType(position));
        bundle.putString("address", mRestaurantRepository.getAddress(position));
        bundle.putString("image", mRestaurantRepository.getImgDetail(mRestaurantRepository.getName(position)));
        navController.navigate(R.id.action_nav_list_to_nav_detail, bundle);
    }
}