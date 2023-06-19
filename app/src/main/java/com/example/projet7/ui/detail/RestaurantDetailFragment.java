package com.example.projet7.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projet7.HomeActivity;
import com.example.projet7.databinding.FragmentRestaurantDetailBinding;
import com.example.projet7.model.User;
import com.example.projet7.ui.workmates.WorkmatesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RestaurantDetailFragment extends Fragment {

    private FragmentRestaurantDetailBinding binding;
    FirebaseFirestore mFirebaseFirestore;
    ArrayList<User> mUserArrayList;
    DetailAdapter mAdapter;
    String restaurantId;

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

        restaurantId = getArguments().getString("id");
        binding.name.setText(getArguments().getString("name"));
        binding.type.setText(getArguments().getString("type"));
        binding.address.setText(getArguments().getString("address"));
        Glide.with(requireContext()).load(getArguments().getString("image")).centerCrop().into(binding.image);
        binding.rvDetailWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new DetailAdapter(requireContext(), mUserArrayList);
        binding.rvDetailWorkmates.setAdapter(mAdapter);

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
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                mUserArrayList.add(dc.getDocument().toObject(User.class));
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
