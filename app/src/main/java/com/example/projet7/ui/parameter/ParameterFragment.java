package com.example.projet7.ui.parameter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentParameterBinding;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.HashMap;

public class ParameterFragment extends Fragment {

    private FragmentParameterBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentParameterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        FirebaseService firebaseService = FirebaseService.getInstance();
        HomeViewModel viewModel = new ViewModelProvider(requireActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);

        firebaseService.getUserDatabaseById(viewModel.getEmailUser(), new BaseFirebase() {
            @Override
            public void getUserDatabaseById(HashMap<String, Object> hashMap) {
                super.getUserDatabaseById(hashMap);
                Boolean notification = (Boolean) hashMap.get("notification");
                binding.notificationSwitch.setChecked(Boolean.TRUE.equals(notification));
            }
        });

        binding.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                firebaseService.setUserData(viewModel.getEmailUser(), "notification", true);
            } else {
                firebaseService.setUserData(viewModel.getEmailUser(), "notification", false);
            }
        });
        return view;
    }
}