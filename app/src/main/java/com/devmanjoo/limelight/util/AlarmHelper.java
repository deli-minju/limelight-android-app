package com.devmanjoo.limelight.util;

import android.app.*;
import android.content.*;
import android.os.Build;

public class AlarmHelper {
    public static void scheduleReminder(Context c, int reqCode, long triggerAt, String title){
        Intent i = new Intent(c, AlarmReceiver.class).putExtra("title", title);
        PendingIntent pi = PendingIntent.getBroadcast(
                c, reqCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    public static void createChannel(Context c){
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(
                    "lime", "LimeLight", NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
    }
}
