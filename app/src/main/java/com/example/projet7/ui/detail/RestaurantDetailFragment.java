package com.example.projet7.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.databinding.FragmentRestaurantDetailBinding;
import com.example.projet7.model.Choice;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RestaurantDetailFragment extends Fragment {

    private FragmentRestaurantDetailBinding binding;
    private HomeViewModel viewModel;

    FirebaseFirestore mFirebaseFirestore;
    ArrayList<Choice> mChoiceArrayList;
    DetailAdapter mAdapter;
    String restaurantId;
    String restaurantName;
    String restaurantType;
    String restaurantAddress;
    String restaurantImage;
    FirebaseAuth mFirebaseAuth;
    CollectionReference users;
    CollectionReference choice;

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
        mChoiceArrayList = new ArrayList<>();
        mFirebaseAuth = FirebaseAuth.getInstance();

        users = mFirebaseFirestore.collection("users");
        choice = mFirebaseFirestore.collection("choice");

        viewModel = new ViewModelProvider(getActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);

        restaurantId = getArguments().getString("id");
        restaurantName = getArguments().getString("name");
        restaurantType = getArguments().getString("type");
        restaurantAddress = getArguments().getString("address");
        restaurantImage = getArguments().getString("image");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());
        choice.whereEqualTo("date", currentDate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentChange dc: task.getResult().getDocumentChanges()) {
                                if (dc.getDocument().get("email").equals(viewModel.getEmailUser()) && dc.getDocument().get("id").equals(restaurantId)) {
                                    binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                                }
                            }
                        }
                    }
                });

        favorite(true);
        binding.name.setText(restaurantName);
        binding.type.setText(restaurantType);
        binding.address.setText(restaurantAddress);
        Glide.with(requireContext()).load(restaurantImage).centerCrop().into(binding.image);
        binding.rvDetailWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new DetailAdapter(requireContext(), mChoiceArrayList);
        binding.rvDetailWorkmates.setAdapter(mAdapter);

        binding.fabRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice.whereEqualTo("date", currentDate).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentChange dc: task.getResult().getDocumentChanges()) {
                                        if (dc.getDocument().get("email").equals(viewModel.getEmailUser())) {
                                            if (dc.getDocument().get("id").equals(restaurantId)) {
                                                choice.document(dc.getDocument().getId()).update("id", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant));
                                                        Log.d("Firebase database", "Remove to lunch");
                                                    }
                                                });
                                            } else {
                                                choice.document(dc.getDocument().getId()).update("id", restaurantId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        binding.fabRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                                                        Log.d("Firebase database", "Add to lunch");
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        });

        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite(false);
            }
        });

        EventChangeListener();

        return view;
    }

    private void favorite(Boolean choice) {
        mFirebaseFirestore.collection("users").whereArrayContains("favorite", restaurantId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (!task.getResult().isEmpty()) {
                                for (int i=0; i<task.getResult().size(); i++) {
                                    if (task.getResult().getDocuments().get(i).getId().equals(viewModel.getEmailUser())) {
                                        if (choice) {
                                            binding.favoriteSymbol.setVisibility(View.VISIBLE);
                                        } else {
                                            mFirebaseFirestore.collection("users").document(viewModel.getEmailUser()).update("favorite", FieldValue.arrayRemove(restaurantId))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            binding.favoriteSymbol.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            } else {
                                if (choice) {
                                    binding.favoriteSymbol.setVisibility(View.GONE);
                                } else {
                                    mFirebaseFirestore.collection("users").document(viewModel.getEmailUser()).update("favorite", FieldValue.arrayUnion(restaurantId))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    binding.favoriteSymbol.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    private void EventChangeListener() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());

        mFirebaseFirestore.collection("choice").whereEqualTo("date", currentDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {
                    return;
                }
                for (DocumentChange dc: value.getDocumentChanges()) {
                    if (dc.getDocument().get("id").equals(restaurantId) && !dc.getDocument().get("email").equals(viewModel.getEmailUser())) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            mChoiceArrayList.add(dc.getDocument().toObject(Choice.class));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
                if (!mChoiceArrayList.isEmpty()) {
                    binding.tvEmptyRV.setVisibility(View.GONE);
                    binding.rvDetailWorkmates.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyRV.setVisibility(View.VISIBLE);
                    binding.rvDetailWorkmates.setVisibility(View.GONE);
                }
            }
        });
//        mFirebaseFirestore.collection("users")
//                .whereEqualTo("idChoice", restaurantId)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Log.d("TAG", "onEvent:" + error.getMessage());
//                            return;
//                        }
//                        for (DocumentChange dc : value.getDocumentChanges()) {
//                            switch (dc.getType()) {
//                                case ADDED:
//                                    if (!dc.getDocument().getId().equals(viewModel.getEmailUser())) {
//                                        mUserArrayList.add(dc.getDocument().toObject(User.class));
//                                    }
//                                case REMOVED:
//                                    mUserArrayList.remove(dc.getDocument().toObject(User.class));
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                        if (!mUserArrayList.isEmpty()) {
//                            binding.tvEmptyRV.setVisibility(View.GONE);
//                            binding.rvDetailWorkmates.setVisibility(View.VISIBLE);
//                        } else {
//                            binding.tvEmptyRV.setVisibility(View.VISIBLE);
//                            binding.rvDetailWorkmates.setVisibility(View.GONE);
//                        }
//                    }
//                });

    }

    @Override
    public void onResume() {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public void onStop() {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        super.onStop();
    }
}
