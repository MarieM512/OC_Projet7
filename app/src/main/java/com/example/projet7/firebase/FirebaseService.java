package com.example.projet7.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseService {

    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference users = mFirebaseFirestore.collection("users");
    CollectionReference choice = mFirebaseFirestore.collection("choice");
    private static volatile FirebaseService INSTANCE = null;

    public static FirebaseService getInstance() {
        if (INSTANCE == null) {
            synchronized (FirebaseService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FirebaseService();
                }
            }
        }
        return INSTANCE;
    }

    public void getUserDatabaseById(String documentId, FirebaseCallback callback) {
        HashMap<String, Object> user = new HashMap<>();
        users.document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.put("name", Objects.requireNonNull(task.getResult().get("name")).toString());
                user.put("email", Objects.requireNonNull(task.getResult().get("email")).toString());
                String image = (String) task.getResult().get("image");
                if (image == null) {
                    image = "";
                }
                user.put("image", image);
                user.put("notification", task.getResult().get("notification"));
                user.put("favorite", task.getResult().get("favorite"));
                callback.getUserDatabaseById(user);
            }
        });
    }
}
