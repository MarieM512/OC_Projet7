package com.example.projet7.ui.workmates;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projet7.R;
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    Context mContext;
    ArrayList<User> mUserArrayList;
    HomeViewModel mViewModel;
    private FirebaseFirestore mFirebaseFirestore;
    private String information;
    private Boolean isEating = false;
    private String type;
    private String name;

    public WorkmatesAdapter(Context context, ArrayList<User> userArrayList, HomeViewModel viewModel) {
        mContext = context;
        mUserArrayList = userArrayList;
        mViewModel = viewModel;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        return new WorkmatesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.workmates_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User user = mUserArrayList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(new Date());

        mFirebaseFirestore.collection("choice").whereEqualTo("date", currentDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        if (document.get("email").equals(user.getEmail())) {
                            if (!document.get("id").equals("")) {
                                isEating = true;
                                type = mViewModel.getLunch(document.get("id").toString()).get("type");
                                name = mViewModel.getLunch(document.get("id").toString()).get("name");
                                break;
                            }
                        }
                    }
                    if (isEating) {
                        information = user.getName() + " " + mContext.getString(R.string.list_choice) + " " + type + " (" + name + ")";
                        holder.detail.setEnabled(true);
                        holder.detail.setTypeface(Typeface.DEFAULT);
                        isEating = false;
                    } else {
                        information = user.getName() + " " + mContext.getString(R.string.list_no_choice);
                        holder.detail.setEnabled(false);
                        holder.detail.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    }
                    holder.detail.setText(information);
                }
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
