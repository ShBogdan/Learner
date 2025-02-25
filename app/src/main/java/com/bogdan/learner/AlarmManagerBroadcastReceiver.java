package com.bogdan.learner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    final int morningId = 33;
    final int eveningId = 44;

    public AlarmManagerBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String notifyMorning = context.getString(R.string.notify_send_morning);
        String notifyEvening = context.getString(R.string.notify_send_evening);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getBoolean("NOTIFY")) {
                notification(context, notifyMorning);
                Toast.makeText(context, "MORNING Notify", Toast.LENGTH_SHORT).show();
            }
            if (!extras.getBoolean("NOTIFY")) {
                notification(context, notifyEvening);
                Toast.makeText(context, "EVENING Notify", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void setMorningAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("NOTIFY", true);
        PendingIntent pi = PendingIntent.getBroadcast(context, morningId, intent, PendingIntent.FLAG_MUTABLE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 7);
        time.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();
        if (time.getTimeInMillis() < now.getTimeInMillis())
            time.add(Calendar.DAY_OF_MONTH, 1);//TimeUnit.DAYS.toMillis(1)
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pi);
    }

    void setEveningAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("NOTIFY", false);
        PendingIntent pi = PendingIntent.getBroadcast(context, eveningId, intent, PendingIntent.FLAG_MUTABLE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 20);
        time.set(Calendar.MINUTE, 0);
        Calendar now = Calendar.getInstance();
        if (time.getTimeInMillis() < now.getTimeInMillis())
            time.add(Calendar.DAY_OF_MONTH, 1);//TimeUnit.DAYS.toMillis(1)
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pi);
    }

    public void cancelMorning(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, morningId, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void cancelEvening(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, eveningId, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    void notification(Context context, String massage) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        builder.setSmallIcon(R.drawable.notify)
                .setContentTitle("Hello!")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText(massage)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
