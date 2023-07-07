package com.example.projet7.ui.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.model.User;
import com.example.projet7.ui.workmates.WorkmatesViewHolder;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    Context mContext;
    ArrayList<User> mUserArrayList;

    public DetailAdapter(Context context, ArrayList<User> userArrayList) {
        mContext = context;
        mUserArrayList = userArrayList;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        User user = mUserArrayList.get(position);
        holder.detail.setText(user.getName() + " " + mContext.getString(R.string.detail_join));
        holder.detail.setEnabled(true);
        holder.detail.setTypeface(Typeface.DEFAULT);
        if (user.getPhoto() == null) {
            holder.image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(mContext).load(user.getPhoto()).centerCrop().into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mUserArrayList.size();
    }
}
