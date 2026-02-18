package com.sinan.hadisimvar;

import android.app.Application;
import android.content.SharedPreferences;

import com.sinan.hadisimvar.utils.NotificationHelper;
import com.sinan.hadisimvar.utils.NotificationScheduler;

public class HadisApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Bildirim kanallarını oluştur
        NotificationHelper.createNotificationChannels(this);

        // AlarmManager ile günlük hadis bildirimini kur
        // Kaydedilmiş saati oku (varsayılan: 09:00)
        SharedPreferences prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        int hour = prefs.getInt("notif_hour", 9);
        int minute = prefs.getInt("notif_minute", 0);
        NotificationScheduler.scheduleDaily(this, hour, minute);
    }
}
