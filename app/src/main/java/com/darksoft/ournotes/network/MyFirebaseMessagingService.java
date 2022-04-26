package com.darksoft.ournotes.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.darksoft.ournotes.R;
import com.darksoft.ournotes.ui.activity.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "ServicioFirebase";
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String texto = remoteMessage.getNotification().getBody();

        final String channel_id = "notificaciones_ournotes";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channel_id, "my_notication",
                    NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            //Creamos la notificacion
            Notification.Builder notifacion = new Notification.Builder(this, channel_id)
                    .setContentTitle(title)
                    .setContentText(texto)
                    .setSmallIcon(R.drawable.or)
                    .setAutoCancel(true);

            NotificationManagerCompat.from(this).notify(1, notifacion.build());
        }else{
            sendNotification(title, texto);
        }

    }

    private void sendNotification(String title, String msg) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MyNotification.NOTIFICATION_ID,
                intent, PendingIntent.FLAG_ONE_SHOT);

        MyNotification myNotification = new MyNotification(this, MyNotification.CHANNEL_ID_NOTIFICATIONS);
        myNotification.build(R.drawable.or, title, msg, pendingIntent);
        myNotification.addChannel("Notificaciones", NotificationManager.IMPORTANCE_HIGH);
        myNotification.createChannelGroup(MyNotification.CHANNEL_GROUP_GENERAL, R.string.notificacionGrupal);
        myNotification.show(MyNotification.NOTIFICATION_ID);


    }
}
