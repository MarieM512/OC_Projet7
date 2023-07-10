package com.example.projet7.injection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.projet7.data.OkhttpService;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.example.projet7.data.RestaurantRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RestaurantRepository mRestaurantRepository;
    private final FirebaseService mFirebaseService;
    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    public ViewModelFactory(Context context) {
        OkhttpService okhttpService = OkhttpService.getInstance();
        mFirebaseService = FirebaseService.getInstance();
        this.mRestaurantRepository = new RestaurantRepository(okhttpService);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mRestaurantRepository, mFirebaseService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
