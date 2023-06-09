package com.example.projet7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.model.Restaurant;


import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    Context mContext;
    List<Restaurant> mRestaurantList;
    private RestaurantRepository mRestaurantRepository = RestaurantRepository.getInstance();

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        mContext = context;
        mRestaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(mContext).inflate(R.layout.restaurant_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.name.setText(mRestaurantRepository.getName(position));
        holder.detail.setText(mRestaurantRepository.getType(position));
        holder.address.setText(mRestaurantRepository.getAddress(position));
        Glide.with(mContext).load(mRestaurantRepository.getImg(mRestaurantRepository.getName(position))).centerCrop().into(holder.image);
        holder.distance.setText(mRestaurantRepository.getDistance(position));
        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
