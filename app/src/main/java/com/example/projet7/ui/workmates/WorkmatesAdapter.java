package com.example.projet7.ui.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet7.R;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    Context mContext;

    public WorkmatesAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        holder.detail.setText("Test");
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
