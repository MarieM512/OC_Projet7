package com.example.projet7.ui.workmates;

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
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private final Context mContext;
    private final ArrayList<User> mUserArrayList;
    private final HomeViewModel viewModel;
    private final FirebaseService mFirebaseService;
    private String information;

    public WorkmatesAdapter(Context context, ArrayList<User> userArrayList, HomeViewModel viewModel, FirebaseService firebaseService) {
        mContext = context;
        mUserArrayList = userArrayList;
        this.viewModel = viewModel;
        mFirebaseService = firebaseService;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User user = mUserArrayList.get(position);

        mFirebaseService.getChoiceUserLive(new BaseFirebase() {
            @Override
            public void getHashMapStringString(HashMap<String, String> hashMap) {
                super.getHashMapStringString(hashMap);
                if (hashMap.containsKey(user.getEmail()) && !Objects.equals(hashMap.get(user.getEmail()), "")) {
                    String id = hashMap.get(user.getEmail());
                    String type = viewModel.getLunchById(id).get("type");
                    String name = viewModel.getLunchById(id).get("name");
                    information = user.getName() + " " + mContext.getString(R.string.list_choice) + " " + type + " (" + name + ")";
                    holder.detail.setEnabled(true);
                    holder.detail.setTypeface(Typeface.DEFAULT);
                } else {
                    information = user.getName() + " " + mContext.getString(R.string.list_no_choice);
                    holder.detail.setEnabled(false);
                    holder.detail.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                }
                holder.detail.setText(information);
            }
        });

        if (user.getImage() == null) {
            holder.image.setImageResource(R.drawable.ic_workmates);
        } else {
            Glide.with(mContext).load(user.getImage()).centerCrop().into(holder.image);
        }
}

    @Override
    public int getItemCount() {
        return mUserArrayList.size();
    }
}
