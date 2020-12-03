package com.example.smartsumparking.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.smartsumparking.R;
import com.example.smartsumparking.activities.GmapActivity;

public class Notification_receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent=new Intent(context, GmapActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
String avaliableSpaces=intent.getStringExtra("avaliableSpaces");
        PendingIntent pendingIntent=PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap pic= BitmapFactory.decodeResource(context.getResources(), R.drawable.sumpic);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Broj slobodnih mjesta je "+avaliableSpaces)
                .setLargeIcon(pic)
                .setColor(Color.BLUE)
                .setVibrate(new long[] {0, 1000, 200,1000 })
                .setLights(Color.MAGENTA, 500, 500)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);
        if (intent.getAction().equals("MY_NOTIFICATION_MESSAGE")) {
        notificationManager.notify(100,builder.build());
            Log.i("Notify", "Alarm");
        }
    }
}
