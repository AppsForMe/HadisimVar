package com.sinan.hadisimvar.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.ui.home.MainActivity;

public class NotificationHelper {

    public static final String CHANNEL_ID_DAILY = "daily_hadith_channel";
    public static final String CHANNEL_NAME_DAILY = "Günlük Hadis";
    public static final String CHANNEL_ID_PRAYER = "prayer_time_channel";
    public static final String CHANNEL_NAME_PRAYER = "Namaz Vakti";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // Günlük Hadis Kanalı
            NotificationChannel channelDaily = new NotificationChannel(
                    CHANNEL_ID_DAILY,
                    CHANNEL_NAME_DAILY,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelDaily.setDescription("Her gün yeni bir hadis bildirimi.");
            manager.createNotificationChannel(channelDaily);

            // Namaz Vakti Kanalı
            NotificationChannel channelPrayer = new NotificationChannel(
                    CHANNEL_ID_PRAYER,
                    CHANNEL_NAME_PRAYER,
                    NotificationManager.IMPORTANCE_HIGH);
            channelPrayer.setDescription("Namaz vakti hatırlatıcıları.");
            channelPrayer.enableLights(true);
            channelPrayer.setLightColor(Color.GREEN);
            manager.createNotificationChannel(channelPrayer);
        }
    }

    public static void showNotification(Context context, String title, String message, String channelId,
                                        int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_mosque) // Eğer mosque ikonu yoksa hata verebilir, var kabul ediyoruz.
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }
}
