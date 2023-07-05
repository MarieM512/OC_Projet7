package com.example.projet7.ui.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    Context mContext;
    List<Restaurant> mRestaurantList;
    private HomeViewModel mHomeViewModel;
    private RecyclerViewInterface mRecyclerViewInterface;
    FirebaseFirestore mFirebaseFirestore;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, RecyclerViewInterface recyclerViewInterface, HomeViewModel viewModel) {
        mContext = context;
        mRestaurantList = restaurantList;
        mRecyclerViewInterface = recyclerViewInterface;
        mHomeViewModel = viewModel;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        return new RestaurantViewHolder(LayoutInflater.from(mContext).inflate(R.layout.restaurant_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mFirebaseFirestore.collection("users").whereEqualTo("idChoice", mHomeViewModel.getId(position)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                int size = task.getResult().size();
                                for (int i = 0; i<task.getResult().size(); i++) {
                                    if (task.getResult().getDocuments().get(i).getId().equals(mHomeViewModel.getEmailUser())) {
                                        size -= 1;
                                    }
                                }
                                if (size > 0) {
                                    holder.reserved.setVisibility(View.VISIBLE);
                                    holder.reserved.setText("(" + size + ")");
                                }
                            }
                        } else {
                            Log.d("TAG", "onComplete: ", task.getException());
                        }
                    }
                });

        holder.name.setText(mHomeViewModel.getName(position));
        holder.detail.setText(mHomeViewModel.getType(position));
        holder.address.setText(mHomeViewModel.getAddress(position));
        Glide.with(mContext).load(mHomeViewModel.getImgRV(position)).centerCrop().into(holder.image);
        holder.distance.setText(mHomeViewModel.getDistance(position));
        holder.itemView.setOnClickListener(v -> {
            mRecyclerViewInterface.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
