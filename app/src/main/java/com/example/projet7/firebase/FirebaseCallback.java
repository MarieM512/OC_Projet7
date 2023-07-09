package com.example.projet7.firebase;

import com.example.projet7.model.Choice;
import com.example.projet7.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public interface FirebaseCallback {
    void getUserDatabaseById(HashMap<String, Object> hashMap);
    void getChoiceDataByCurrentDate(HashMap<String, String> hashMap);
    void getUserIsEating(ArrayList<Choice> choiceArrayList);
    void getUserNumberForRestaurant(int size);
    void getAllUserExceptSelf(ArrayList<User>userArrayList);
}
