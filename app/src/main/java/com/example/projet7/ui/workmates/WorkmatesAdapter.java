package com.example.projet7.ui.workmates;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.model.User;

import java.util.ArrayList;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    Context mContext;
    ArrayList<User> mUserArrayList;

    public WorkmatesAdapter(Context context, ArrayList<User> userArrayList) {
        mContext = context;
        mUserArrayList = userArrayList;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User user = mUserArrayList.get(position);
        String information;
        if (Objects.equals(user.getNameChoice(), "")) {
            information = user.getName() + " hasn't decided yet";
        } else {
            information = user.getName() + " is eating " + user.getTypeChoice() + " (" + user.getNameChoice() + ")";
            holder.detail.setEnabled(true);
            holder.detail.setTypeface(Typeface.DEFAULT);
        }
        holder.detail.setText(information);
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
