package com.example.projet7.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projet7.HomeActivity;
import com.example.projet7.R;
import com.example.projet7.firebase.BaseFirebase;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.model.Choice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PushNotificationService extends FirebaseMessagingService {

    private final FirebaseService mFirebaseService = FirebaseService.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final ArrayList<String> workmatesList = new ArrayList<>();
    private String name;
    private String address;
    private String workmates;
    private int size;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {

            mFirebaseService.getUserDatabaseById(user.getEmail(), new BaseFirebase() {
                @Override
                public void getHashMapStringObject(HashMap<String, Object> hashMap) {
                    super.getHashMapStringObject(hashMap);
                    if (Objects.equals(hashMap.get("notification"), true)) {
                        mFirebaseService.getIdChoiceOfUser(user.getEmail(), new BaseFirebase() {
                            @Override
                            public void getId(String id) {
                                super.getId(id);
                                if (!id.isEmpty()) {
                                    mFirebaseService.getInfoChoice(id, new BaseFirebase() {
                                        @Override
                                        public void getHashMapStringString(HashMap<String, String> hashMap) {
                                            super.getHashMapStringString(hashMap);
                                            name = hashMap.get("name");
                                            address = hashMap.get("address");
                                        }
                                    });
                                    mFirebaseService.getUserIsEating(user.getEmail(), id, new BaseFirebase() {
                                        @Override
                                        public void getArrayListChoice(ArrayList<Choice> choiceArrayList) {
                                            super.getArrayListChoice(choiceArrayList);
                                            size = choiceArrayList.size();
                                            if (size == 0) {
                                                workmates = String.join(", ", workmatesList);
                                                sendNotification(name, address, workmates);
                                            }
                                            for (Choice choice: choiceArrayList) {
                                                mFirebaseService.getUserDatabaseById(choice.getEmail(), new BaseFirebase() {
                                                    @Override
                                                    public void getHashMapStringObject(HashMap<String, Object> hashMap) {
                                                        super.getHashMapStringObject(hashMap);
                                                        workmatesList.add(Objects.requireNonNull(hashMap.get("name")).toString());
                                                        size--;
                                                        if (size == 0) {
                                                            workmates = String.join(", ", workmatesList);
                                                            sendNotification(name, address, workmates);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void sendNotification(String name, String address, String workmates) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String title = getString(R.string.notif_title) + " " + name;
        String body;
        if (workmates.isEmpty()) {
            body = getString(R.string.notif_body_start) + " " + address;
        } else {
            body = getString(R.string.notif_body_start) + " " + address + " " + getString(R.string.notif_body_middle) + " " + workmates;
        }
        String channelId = "channelId";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nameChannel = getString(R.string.channel_name);
            String descriptionChannel = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, nameChannel, importance);
            channel.setDescription(descriptionChannel);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(100, notificationBuilder.build());
        }
    }
}
