package com.example.projet7.ui.workmates;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentWorkmatesBinding;
import com.example.projet7.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;
    FirebaseFirestore mFirebaseFirestore;
    ArrayList<User> mUserArrayList;
    WorkmatesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mUserArrayList = new ArrayList<>();
        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new WorkmatesAdapter(requireContext(), mUserArrayList);
        binding.rvWorkmates.setAdapter(mAdapter);

        EventChangeListener();
        return view;
    }

    private void EventChangeListener() {
        mFirebaseFirestore.collection("users").orderBy("idChoice", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("TAG", "onEvent:" + error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if (!dc.getDocument().getId().equals(userEmail)) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    mUserArrayList.add(dc.getDocument().toObject(User.class));
                                }
                                if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                    mUserArrayList.set(dc.getOldIndex(), dc.getDocument().toObject(User.class));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}