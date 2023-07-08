package com.example.projet7.firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.projet7.model.Choice;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class FirebaseService {

    private final FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference users = mFirebaseFirestore.collection("users");
    private final CollectionReference choice = mFirebaseFirestore.collection("choice");
    private static volatile FirebaseService INSTANCE = null;
    private static final String TAG = "Firebase Service";
    private ArrayList<Choice> mChoiceArrayList;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private final String currentDate = sdf.format(new Date());

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

    public void getChoiceDataByCurrentDate(FirebaseCallback callback) {
        HashMap<String, String> currentChoice = new HashMap<>();
        choice.whereEqualTo("date", currentDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    currentChoice.put(Objects.requireNonNull(document.get("email")).toString(), Objects.requireNonNull(document.get("id")).toString());
                }
                callback.getChoiceDataByCurrentDate(currentChoice);
            }
        });
    }

    public void setChoiceId(String email, String id) {
        choice.whereEqualTo("date", currentDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    if (Objects.equals(document.get("email"), email)) {
                        choice.document(document.getId()).update("id", id).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Id choice changed for " + id);
                            }
                        });
                        break;
                    }
                }
            }
        });
    }

    public void setUserData(String email, String key, Object value) {
        users.document(email).update(key, value).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, key + " has changed for " + value);
            }
        });
    }

    public void getUserIsEating(String email, String id, FirebaseCallback callback) {
        mChoiceArrayList = new ArrayList<>();
        choice.whereEqualTo("date", currentDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    if (Objects.equals(document.get("id"), id) && !Objects.equals(document.get("email"), email)) {
                        mChoiceArrayList.add(document.toObject(Choice.class));
                    }
                }
                callback.getUserIsEating(mChoiceArrayList);
            }
        });
    }
}
