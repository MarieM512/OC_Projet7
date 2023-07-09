package com.example.projet7.ui.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final Context mContext;
    private final List<Restaurant> mRestaurantList;
    private final HomeViewModel viewModel;
    private final RecyclerViewInterface mRecyclerViewInterface;
    private FirebaseService mFirebaseService;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, RecyclerViewInterface recyclerViewInterface, HomeViewModel viewModel, FirebaseService firebaseService) {
        mContext = context;
        mRestaurantList = restaurantList;
        mRecyclerViewInterface = recyclerViewInterface;
        this.viewModel = viewModel;
        mFirebaseService = firebaseService;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(mContext).inflate(R.layout.restaurant_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, @SuppressLint("RecyclerView") int position) {

        mFirebaseService.getUserNumberForRestaurant(viewModel.getId(position), viewModel.getEmailUser(), new BaseFirebase() {
            @Override
            public void getUserNumberForRestaurant(int size) {
                super.getUserNumberForRestaurant(size);
                if (size > 0) {
                    holder.reserved.setVisibility(View.VISIBLE);
                    holder.reserved.setText("(" + size + ")");
                }
            }
        });

        holder.name.setText(viewModel.getName(position));
        holder.detail.setText(viewModel.getType(position));
        holder.address.setText(viewModel.getAddress(position));
        Glide.with(mContext).load(viewModel.getImgRV(position)).centerCrop().into(holder.image);
        holder.distance.setText(viewModel.getDistance(position));
        holder.itemView.setOnClickListener(v -> {
            mRecyclerViewInterface.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
