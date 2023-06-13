package com.example.projet7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projet7.databinding.FragmentRestaurantDetailBinding;

public class RestaurantDetailFragment extends Fragment {

    private FragmentRestaurantDetailBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.name.setText(getArguments().getString("name"));
        binding.type.setText(getArguments().getString("type"));
        binding.address.setText(getArguments().getString("address"));
        Glide.with(requireContext()).load(getArguments().getString("image")).centerCrop().into(binding.image);

        return view;
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).getSupportActionBar().hide();
        super.onResume();
    }

    @Override
    public void onStop() {
        ((HomeActivity) getActivity()).getSupportActionBar().show();
        super.onStop();
    }
}
