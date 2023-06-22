package com.example.projet7.ui.workmates;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet7.R;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView detail;

    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.imageWorkmates);
        detail = (TextView) itemView.findViewById(R.id.infoWorkmates);
    }
}
