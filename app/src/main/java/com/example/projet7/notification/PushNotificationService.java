package com.example.projet7.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projet7.HomeActivity;
import com.example.projet7.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class PushNotificationService extends FirebaseMessagingService {

    FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> workmatesList = new ArrayList<>();
    private String channelId = "channelId";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {
            mFirebaseFirestore.collection("users").document(user.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Boolean notification = (Boolean) task.getResult().getData().get("notification");
                                String idChoice = (String) task.getResult().getData().get("idChoice");
                                Boolean choice = !idChoice.isEmpty();
                                if (notification && choice) {
                                    String nameChoice = (String) task.getResult().getData().get("nameChoice");
                                    String addressChoice = (String) task.getResult().getData().get("addressChoice");
                                    mFirebaseFirestore.collection("users").whereEqualTo("idChoice", idChoice)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    for (DocumentChange dc : value.getDocumentChanges()) {
                                                        if (!dc.getDocument().getId().equals(user.getEmail())) {
                                                            workmatesList.add(dc.getDocument().get("name").toString());
                                                        }
                                                    }
                                                    String workmates = String.join(", ", workmatesList);
                                                    sendNotification(nameChoice, addressChoice, workmates);
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }


    private void sendNotification(String name, String address, String workmates) {
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String title = getString(R.string.notif_title) + " " + name;
        String body = getString(R.string.notif_body_start) + " " + address + " " + getString(R.string.notif_body_middle) + " " + workmates;
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
