package com.example.projet7.ui.workmates;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projet7.R;
import com.example.projet7.databinding.FragmentWorkmatesBinding;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.User;
import com.example.projet7.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class WorkmatesFragment extends Fragment implements RecyclerViewItemClick {

    private FragmentWorkmatesBinding binding;
    private FirebaseService mFirebaseService;
    private WorkmatesAdapter mAdapter;
    private HomeViewModel viewModel;
    private Integer size;
    private NavController mNavController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity(), getDefaultViewModelProviderFactory()).get(HomeViewModel.class);
        mFirebaseService = FirebaseService.getInstance();

        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(requireContext()));
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        mFirebaseService.getAllUserExceptSelf(viewModel.getEmailUser(), new BaseFirebase() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void getArrayListUser(ArrayList<User> userArrayList) {
                super.getArrayListUser(userArrayList);
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                size = userArrayList.size();
                for (User user: userArrayList) {
                    mFirebaseService.getChoiceUserLive(new BaseFirebase() {
                        @Override
                        public void getHashMapStringString(HashMap<String, String> hashMap) {
                            super.getHashMapStringString(hashMap);
                            HashMap<String, String> stringHashMap = new HashMap<>();
                            if (hashMap.containsKey(user.getEmail())) {
                                stringHashMap.put("id", hashMap.get(user.getEmail()));
                                stringHashMap.put("type", viewModel.getLunchById(hashMap.get(user.getEmail())).get("type"));
                                stringHashMap.put("name", viewModel.getLunchById(hashMap.get(user.getEmail())).get("name"));
                            } else {
                                stringHashMap.put("id", "");
                                stringHashMap.put("type", "");
                                stringHashMap.put("name", "");
                            }
                            stringHashMap.put("userName", user.getName());
                            stringHashMap.put("image", user.getImage());
                            arrayList.add(stringHashMap);
                            size--;
                            if (size == 0) {
                                Collections.sort(arrayList, (o1, o2) -> {
                                    String id1 = o1.get("id");
                                    String id2 = o2.get("id");
                                    if (id1.compareTo(id2) < 0) {
                                        return 1;
                                    } else if (id1.compareTo(id2) > 0) {
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                });
                                mAdapter = new WorkmatesAdapter(requireContext(), arrayList, WorkmatesFragment.this);
                                binding.rvWorkmates.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void clickListener(String id) {
        mNavController.navigate(R.id.nav_detail, viewModel.goToRestaurantById(false, id));
    }
}