package com.example.iconnect;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//public class FirebaseService extends FirebaseMessagingService {
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//        super.onMessageReceived(message);
//
//        Log.d("FirebaseService", "From: " + message.getFrom());
//
//        RemoteMessage.Notification notification = message.getNotification();
//        if (notification.getTitle().equals("video")||notification.getTitle().equals("audio")) {
//            Intent intent = new Intent(this, IncomingCallActivity.class);
//            intent.putExtra("senderId", notification.getBody());
//            intent.putExtra("type", notification.getTitle());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }else{
//            sendNotification(notification.getTitle(), notification.getBody());
//        }
//    }
//    private void sendNotification(String title, String messageBody) {
//        Intent intent = new Intent(this, ChatsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
//
//        String channelId = "1";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_send)
//                        .setContentTitle(title)
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//}





public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d("FirebaseService", "From: " + message.getFrom());

        // Handle the notification payload if available
        RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            Log.d("FirebaseService", "Notification title: " + notification.getTitle());
            Log.d("FirebaseService", "Notification body: " + notification.getBody());

            // If the notification is a video or audio call
            if ("Video".equals(notification.getTitle()) || "Audio".equals(notification.getTitle())) {
                String senderId = notification.getBody();  // Assuming sender ID is in the body
                Intent intent = new Intent(this, IncomingCallActivity.class);
                intent.putExtra("senderId", senderId);
                intent.putExtra("type", notification.getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                // Handle other notifications if necessary
                sendNotification(notification.getTitle(), notification.getBody());
            }
        }

        // Handle the data payload if available
        if (message.getData().size() > 0) {
            String type = message.getData().get("type");
            String senderId = message.getData().get("senderId");

            Log.d("FirebaseService", "Message data: " + type);
            Log.d("FirebaseService", "Sender ID: " + senderId);

            // If it's a video or audio call, navigate to the incoming call activity
            if ("Video".equals(type) || "Audio".equals(type)) {
                Intent intent = new Intent(this, IncomingCallActivity.class);
                intent.putExtra("senderId", senderId);
                intent.putExtra("type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    // Method to show notifications when the app is in the background
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ChatsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_send)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since Android Oreo and above require a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
