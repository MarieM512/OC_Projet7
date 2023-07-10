package com.example.projet7.ui.workmates;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentWorkmatesBinding;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private FirebaseService mFirebaseService;
    private WorkmatesAdapter mAdapter;
    private HomeViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        mFirebaseService = FirebaseService.getInstance();

        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mFirebaseService.getAllUserExceptSelf(viewModel.getEmailUser(), new BaseFirebase() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void getArrayListUser(ArrayList<User> userArrayList) {
                super.getArrayListUser(userArrayList);
                mAdapter = new WorkmatesAdapter(requireContext(), userArrayList, viewModel, mFirebaseService);
                binding.rvWorkmates.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}