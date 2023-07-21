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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final Context mContext;
    private final List<Restaurant> mRestaurantList;
    private final RecyclerViewInterface mRecyclerViewInterface;
    private final ArrayList<HashMap<String, Object>> mArrayList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, RecyclerViewInterface recyclerViewInterface, ArrayList<HashMap<String, Object>> arrayList) {
        mContext = context;
        mRestaurantList = restaurantList;
        mRecyclerViewInterface = recyclerViewInterface;
        mArrayList = arrayList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(mContext).inflate(R.layout.restaurant_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Integer size = (Integer) mArrayList.get(position).get("size");
        if (size > 0) {
            holder.reserved.setVisibility(View.VISIBLE);
            holder.reserved.setText("(" + size + ")");
        } else {
            holder.reserved.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(mArrayList.get(position).get("name").toString());
        holder.detail.setText(mArrayList.get(position).get("detail").toString());
        holder.address.setText(mArrayList.get(position).get("address").toString());
        Glide.with(mContext).load(mArrayList.get(position).get("img")).centerCrop().into(holder.image);
        holder.distance.setText(mArrayList.get(position).get("distance").toString());
        holder.itemView.setOnClickListener(v -> {
            mRecyclerViewInterface.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
