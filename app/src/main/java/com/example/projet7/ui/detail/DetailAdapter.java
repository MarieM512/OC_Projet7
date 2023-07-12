package com.example.projet7.ui.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.Choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    private final Context mContext;

    private final ArrayList<HashMap<String,Object>> mChoiceArrayList;
    private final FirebaseService mFirebaseService;

    public DetailAdapter(Context context, ArrayList<HashMap<String, Object>> userArrayList, FirebaseService firebaseService) {
        mContext = context;
        mChoiceArrayList = userArrayList;
        mFirebaseService = firebaseService;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        HashMap<String, Object> hashMap = mChoiceArrayList.get(position);

        String name = Objects.requireNonNull(hashMap.get("name")).toString();
        String image = Objects.requireNonNull(hashMap.get("image")).toString();
        holder.detail.setText(name + " " + mContext.getString(R.string.detail_join));
        holder.detail.setEnabled(true);
        holder.detail.setTypeface(Typeface.DEFAULT);
        if (image.isEmpty()) {
            holder.image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(mContext).load(image).centerCrop().into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mChoiceArrayList.size();
    }
}
