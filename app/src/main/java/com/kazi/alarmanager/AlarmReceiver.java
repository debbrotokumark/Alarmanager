package com.kazi.alarmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        playAlarmSound(context);
        createNotificationChannel(context);
        createNotification(context);
    }

    private void playAlarmSound(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmSound);
        if (ringtone != null) {
            ringtone.play();
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Notification";
            String description = "Notification for Alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alarm")
                .setContentText("It's time for your alarm!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e("AlarmReceiver", "NotificationManager is null. Notification not shown.");
        }
    }

}
