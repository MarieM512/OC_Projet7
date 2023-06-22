package com.example.projet7.ui.detail;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projet7.HomeActivity;
import com.example.projet7.R;
import com.example.projet7.databinding.FragmentRestaurantDetailBinding;
import com.example.projet7.model.User;
import com.example.projet7.ui.workmates.WorkmatesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantDetailFragment extends Fragment {

    private FragmentRestaurantDetailBinding binding;
    FirebaseFirestore mFirebaseFirestore;
    ArrayList<User> mUserArrayList;
    DetailAdapter mAdapter;
    String restaurantId;
    String restaurantName;
    String restaurantType;
    FirebaseAuth mFirebaseAuth;
    CollectionReference users;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mUserArrayList = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();
        users = mFirebaseFirestore.collection("users");
        user = mFirebaseAuth.getCurrentUser();

        restaurantId = getArguments().getString("id");
        restaurantName = getArguments().getString("name");
        restaurantType = getArguments().getString("type");
        users.document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().get("idChoice").equals(restaurantId)) {
                        binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                    }
                }
            }
        });

        binding.name.setText(restaurantName);
        binding.type.setText(restaurantType);
        binding.address.setText(getArguments().getString("address"));
        Glide.with(requireContext()).load(getArguments().getString("image")).centerCrop().into(binding.image);
        binding.rvDetailWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new DetailAdapter(requireContext(), mUserArrayList);
        binding.rvDetailWorkmates.setAdapter(mAdapter);

        binding.fabRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.get("idChoice").equals(restaurantId)) {
                                mFirebaseFirestore.collection("users").document(user.getEmail()).update(
                                        "idChoice", "",
                                        "nameChoice", "",
                                        "typeChoice", ""
                                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant));
                                        Log.d("Firestore database", "Remove to lunch");
                                    }
                                });
                            } else {
                                mFirebaseFirestore.collection("users").document(user.getEmail()).update(
                                        "idChoice", restaurantId,
                                        "nameChoice", restaurantName,
                                        "typeChoice", restaurantType
                                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                                        Log.d("Firestore database", "Add to lunch");
                                    }
                                });
                            }
                        } else {
                            Log.d("Firestore database", "Failed to add to lunch", task.getException());
                        }
                    }
                });
            }
        });

        EventChangeListener();

        return view;
    }

    private void EventChangeListener() {
        mFirebaseFirestore.collection("users")
                .whereEqualTo("idChoice", restaurantId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("TAG", "onEvent:" + error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    if (!dc.getDocument().getId().equals(user.getEmail())) {
                                        mUserArrayList.add(dc.getDocument().toObject(User.class));
                                    }
                                case REMOVED:
                                    mUserArrayList.remove(dc.getDocument().toObject(User.class));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        if (!mUserArrayList.isEmpty()) {
                            binding.tvEmptyRV.setVisibility(View.GONE);
                            binding.rvDetailWorkmates.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvEmptyRV.setVisibility(View.VISIBLE);
                            binding.rvDetailWorkmates.setVisibility(View.GONE);
                        }
                    }
                });

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
