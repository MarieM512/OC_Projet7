package com.example.projet7.ui.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.model.Choice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    Context mContext;
    ArrayList<Choice> mChoiceArrayList;
    private FirebaseFirestore mFirebaseFirestore;

    public DetailAdapter(Context context, ArrayList<Choice> userArrayList) {
        mContext = context;
        mChoiceArrayList = userArrayList;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        return new DetailViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Choice choice = mChoiceArrayList.get(position);

        mFirebaseFirestore.collection("users").document(choice.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().get("name").toString();
                    String image = task.getResult().get("image").toString();
                    holder.detail.setText(name + " " + mContext.getString(R.string.detail_join));
                    holder.detail.setEnabled(true);
                    holder.detail.setTypeface(Typeface.DEFAULT);
                    if (image == null) {
                        holder.image.setImageResource(R.drawable.ic_workmates);
                    } else {
                        Glide.with(mContext).load(image).centerCrop().into(holder.image);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChoiceArrayList.size();
    }
}
