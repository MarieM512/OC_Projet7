package com.example.projet7.ui.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());
        mFirebaseFirestore.collection("choice").whereEqualTo("date", currentDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value == null) {
                            return;
                        }
                        int size = 0;
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getDocument().get("id").equals(mHomeViewModel.getId(position))) {
                                size++;
                                if (dc.getDocument().getData().get("email").equals(mHomeViewModel.getEmailUser())) {
                                    size--;
                                }
                            }
                        }
                        if (size > 0) {
                            holder.reserved.setVisibility(View.VISIBLE);
                            holder.reserved.setText("(" + size + ")");
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
