package com.example.projet7.ui.workmates;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.databinding.FragmentWorkmatesBinding;
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;
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
    private HomeViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(getActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mUserArrayList = new ArrayList<>();
        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new WorkmatesAdapter(requireContext(), mUserArrayList, viewModel);
        binding.rvWorkmates.setAdapter(mAdapter);

        EventChangeListener();
        return view;
    }

    private void EventChangeListener() {
        mFirebaseFirestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("TAG", "onEvent:" + error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED && !dc.getDocument().getId().equals(viewModel.getEmailUser())) {
                                mUserArrayList.add(dc.getDocument().toObject(User.class));
                            }
                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                mUserArrayList.set(dc.getNewIndex(), dc.getDocument().toObject(User.class));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}