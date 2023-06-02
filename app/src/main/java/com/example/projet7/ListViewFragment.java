package com.example.projet7;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.model.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListViewFragment extends Fragment {

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
        recyclerView.setAdapter(new RestaurantAdapter(getActivity().getApplicationContext(), restaurants));
        return view;
    }
}