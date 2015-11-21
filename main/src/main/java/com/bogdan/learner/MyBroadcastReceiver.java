package com.bogdan.learner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.NotificationCompat;


public class MyBroadcastReceiver extends BroadcastReceiver {


    public void onReceive(Context context, Intent intent) {

// Установим следующее напоминание.
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
//                intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);

        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        Resources r = context.getResources();
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Learner")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Вы сегодня занимались?")
                .setContentText("Для закрепления материала необходимо ежедневное повторение")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}
