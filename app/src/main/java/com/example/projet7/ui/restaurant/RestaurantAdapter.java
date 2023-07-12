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
import com.example.projet7.model.Restaurant;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final Context mContext;
    private final List<Restaurant> mRestaurantList;
    private final RecyclerViewInterface mRecyclerViewInterface;
    private final ArrayList<Integer> mIntegerArrayList;
    private final HomeViewModel mViewModel;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, RecyclerViewInterface recyclerViewInterface, HomeViewModel viewModel, ArrayList<Integer> integerArrayList) {
        mContext = context;
        mRestaurantList = restaurantList;
        mRecyclerViewInterface = recyclerViewInterface;
        mIntegerArrayList = integerArrayList;
        mViewModel = viewModel;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(mContext).inflate(R.layout.restaurant_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Integer size = mIntegerArrayList.get(position);
        if (size > 0) {
            holder.reserved.setVisibility(View.VISIBLE);
            holder.reserved.setText("(" + size + ")");
        } else {
            holder.reserved.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(mViewModel.getName(position));
        holder.detail.setText(mViewModel.getType(position));
        holder.address.setText(mViewModel.getAddress(position));
        Glide.with(mContext).load(mViewModel.getImgRV(position)).centerCrop().into(holder.image);
        holder.distance.setText(mViewModel.getDistance(position));
        holder.itemView.setOnClickListener(v -> {
            mRecyclerViewInterface.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
