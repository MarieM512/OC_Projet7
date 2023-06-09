package com.example.projet7.ui.restaurant;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet7.R;

import org.w3c.dom.Text;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView detail;
    TextView address;
    TextView distance;
    TextView reserved;
    ImageView image;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.nameCard);
        detail = (TextView) itemView.findViewById(R.id.detailCard);
        address = (TextView) itemView.findViewById(R.id.addressCard);
        distance = (TextView) itemView.findViewById(R.id.distanceCard);
        reserved = (TextView) itemView.findViewById(R.id.reservedCard);
        image = (ImageView) itemView.findViewById(R.id.imageCard);
    }
}
