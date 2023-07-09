package com.example.projet7.firebase;

import com.example.projet7.model.Choice;
import com.example.projet7.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseFirebase implements FirebaseCallback {
    @Override
    public void getUserDatabaseById(HashMap<String, Object> hashMap) {

    }

    @Override
    public void getChoiceDataByCurrentDate(HashMap<String, String> hashMap) {

    }

    @Override
    public void getUserIsEating(ArrayList<Choice> choiceArrayList) {

    }

    @Override
    public void getUserNumberForRestaurant(int size) {

    }

    @Override
    public void getAllUserExceptSelf(ArrayList<User> userArrayList) {

    }
}
