package com.example.projet7.firebase;

import com.example.projet7.model.Choice;
import com.example.projet7.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public interface FirebaseCallback {
    void getHashMapStringObject(HashMap<String, Object> hashMap);
    void getHashMapStringString(HashMap<String, String> hashMap);
    void getArrayListChoice(ArrayList<Choice> choiceArrayList);
    void getSize(int size);
    void getArrayListUser(ArrayList<User> userArrayList);
    void getId(String id);
}
