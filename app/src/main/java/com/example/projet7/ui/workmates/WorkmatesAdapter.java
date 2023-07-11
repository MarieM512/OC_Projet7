package com.example.projet7.ui.workmates;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private final Context mContext;
    private final RecyclerViewItemClick mRecyclerViewItemClick;
    private final ArrayList<HashMap<String, String>> mArrayList;

    public WorkmatesAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, RecyclerViewItemClick recyclerViewItemClick) {
        mContext = context;
        mArrayList = arrayList;
        mRecyclerViewItemClick = recyclerViewItemClick;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        HashMap<String, String> hashMap = mArrayList.get(position);

        String information;
        if (!Objects.equals(hashMap.get("id"), "")) {
            information = hashMap.get("userName") + " " + mContext.getString(R.string.list_choice) + " " + hashMap.get("type") + " (" + hashMap.get("name") + ")";
            holder.detail.setEnabled(true);
            holder.detail.setTypeface(Typeface.DEFAULT);
            holder.itemView.setOnClickListener(v -> mRecyclerViewItemClick.clickListener(hashMap.get("id")));
        } else {
            information = hashMap.get("userName") + " " + mContext.getString(R.string.list_no_choice);
            holder.detail.setEnabled(false);
            holder.detail.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        }
        holder.detail.setText(information);

        if (hashMap.get("image") == null) {
            holder.image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(mContext).load(hashMap.get("image")).centerCrop().into(holder.image);
        }
}

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }
}
