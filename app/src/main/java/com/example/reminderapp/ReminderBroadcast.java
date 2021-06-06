package com.example.reminderapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
        builder.setContentTitle("Events Asshole");
        builder.setContentText("Do Your Fucking Events");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setSmallIcon(android.R.drawable.arrow_up_float);
        builder.setAutoCancel(true);


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(200,builder.build());

    }
}
