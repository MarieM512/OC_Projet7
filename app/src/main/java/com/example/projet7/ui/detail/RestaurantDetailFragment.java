package com.example.projet7.ui.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.databinding.FragmentRestaurantDetailBinding;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.Choice;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RestaurantDetailFragment extends Fragment {

    private FragmentRestaurantDetailBinding binding;
    private HomeViewModel viewModel;
    private FirebaseService mFirebaseService;
    private DetailAdapter mAdapter;
    private String restaurantId;
    private Context mContext;
    private Integer size;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        mContext = requireContext();

        mFirebaseService = FirebaseService.getInstance();
        viewModel = new ViewModelProvider(requireActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        binding.rvDetailWorkmates.setLayoutManager(new LinearLayoutManager(mContext));

        restaurantId = getArguments().getString("id");
        String restaurantName = getArguments().getString("name");
        String restaurantType = getArguments().getString("type");
        String restaurantAddress = getArguments().getString("address");
        String restaurantImage = getArguments().getString("image");

        mFirebaseService.getChoiceDataByCurrentDate(viewModel, new BaseFirebase() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void getHashMapStringString(HashMap<String, String> hashMap) {
                super.getHashMapStringString(hashMap);
                if (Objects.equals(hashMap.get(viewModel.getEmailUser()), restaurantId)) {
                    binding.fabRestaurant.setImageDrawable(mContext.getDrawable(R.drawable.ic_check));
                }
            }
        });

        mFirebaseService.getUserIsEatingLive(viewModel.getEmailUser(), restaurantId, new BaseFirebase() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void getArrayListChoice(ArrayList<Choice> choiceArrayList) {
                super.getArrayListChoice(choiceArrayList);
                ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                size = choiceArrayList.size();
                for (Choice choice: choiceArrayList) {
                    mFirebaseService.getUserDatabaseById(choice.getEmail(), new BaseFirebase() {
                        @Override
                        public void getHashMapStringObject(HashMap<String, Object> hashMap) {
                            super.getHashMapStringObject(hashMap);
                            arrayList.add(hashMap);
                            size--;
                            if (size == 0) {
                                mAdapter = new DetailAdapter(mContext, arrayList);
                                binding.rvDetailWorkmates.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                if (!choiceArrayList.isEmpty()) {
                                    binding.tvEmptyRV.setVisibility(View.GONE);
                                    binding.rvDetailWorkmates.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvEmptyRV.setVisibility(View.VISIBLE);
                                    binding.rvDetailWorkmates.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });

        binding.name.setText(restaurantName);
        binding.type.setText(restaurantType);
        binding.address.setText(restaurantAddress);
        Glide.with(mContext).load(restaurantImage).centerCrop().into(binding.image);
        favorite(false);
        binding.like.setOnClickListener(v -> favorite(true));

        binding.fabRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseService.getChoiceDataByCurrentDate(viewModel, new BaseFirebase() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void getHashMapStringString(HashMap<String, String> hashMap) {
                        super.getHashMapStringString(hashMap);
                        if (Objects.equals(hashMap.get(viewModel.getEmailUser()), restaurantId)) {
                            mFirebaseService.setChoiceId(viewModel.getEmailUser(), "");
                            binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant));
                        } else {
                            mFirebaseService.setChoiceId(viewModel.getEmailUser(), restaurantId);
                            binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        }
                    }
                });
            }
        });

        return view;
    }

    private void favorite(Boolean tapped) {
        mFirebaseService.getUserDatabaseById(viewModel.getEmailUser(), new BaseFirebase() {
            @Override
            public void getHashMapStringObject(HashMap<String, Object> hashMap) {
                super.getHashMapStringObject(hashMap);
                if (((List<?>) Objects.requireNonNull(hashMap.get("favorite"))).contains(restaurantId)) {
                    if (tapped) {
                        binding.favoriteSymbol.setVisibility(View.GONE);
                        mFirebaseService.setUserData(viewModel.getEmailUser(), "favorite", FieldValue.arrayRemove(restaurantId));
                    } else {
                        binding.favoriteSymbol.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (tapped) {
                        mFirebaseService.setUserData(viewModel.getEmailUser(), "favorite", FieldValue.arrayUnion(restaurantId));
                        binding.favoriteSymbol.setVisibility(View.VISIBLE);
                    } else {
                        binding.favoriteSymbol.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        requireActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public void onStop() {
        requireActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        super.onStop();
    }
}
