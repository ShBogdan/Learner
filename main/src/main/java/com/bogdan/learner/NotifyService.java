package com.bogdan.learner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class NotifyService extends Service {
    public NotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "Service is started", Toast.LENGTH_SHORT).show();
    }
}
