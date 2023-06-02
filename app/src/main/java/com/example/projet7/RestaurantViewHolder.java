package com.example.projet7;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView detail;
    TextView address;
    TextView distance;
    ImageView person;
    TextView reserved;
    ImageView image;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.nameCard);
        detail = (TextView) itemView.findViewById(R.id.detailCard);
        address = (TextView) itemView.findViewById(R.id.addressCard);
        distance = (TextView) itemView.findViewById(R.id.distanceCard);
        person = (ImageView) itemView.findViewById(R.id.personCard);
        reserved = (TextView) itemView.findViewById(R.id.reservedCard);
        image = (ImageView) itemView.findViewById(R.id.imageCard);
    }
}
