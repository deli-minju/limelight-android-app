package com.devmanjoo.limelight.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.*;
import androidx.core.app.NotificationCompat;
import com.devmanjoo.limelight.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        Notification n = new NotificationCompat.Builder(context, "lime")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("30분 전 알림")
                .setContentText(title + " 상영 30분 전입니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int)System.currentTimeMillis(), n);
    }
}
