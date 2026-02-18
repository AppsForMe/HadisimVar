package com.sinan.hadisimvar.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.sinan.hadisimvar.receivers.AlarmReceiver;

import java.util.Calendar;

/**
 * AlarmManager kullanarak günlük hadis bildirimlerini zamanlar.
 * WorkManager'a göre çok daha hassas zamanlama sağlar (±1 dakika).
 */
public class NotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final String PREF_NAME = "settings_prefs";
    private static final String KEY_NOTIF_HOUR = "notif_hour";
    private static final String KEY_NOTIF_MINUTE = "notif_minute";

    /**
     * Belirli bir saat için günlük alarm kurar.
     * Eski WorkManager implementasyonuyla uyumlu API.
     */
    public static void scheduleDaily(Context context, int hour, int minute) {
        scheduleExactAlarm(context, hour, minute);
    }

    /**
     * AlarmManager ile kesin zamanlı alarm kurar.
     */
    public static void scheduleExactAlarm(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager null");
            return;
        }

        // Hedef zamanı hesapla
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, hour);
        targetTime.set(Calendar.MINUTE, minute);
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);

        // Eğer saat geçmişse yarına ayarla
        if (targetTime.before(Calendar.getInstance())) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // PendingIntent oluştur
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Önce mevcut alarm'ı iptal et
        alarmManager.cancel(pendingIntent);

        // Yeni alarm kur - cihaz uyku modundayken de çalışır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetTime.getTimeInMillis(),
                    pendingIntent);
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetTime.getTimeInMillis(),
                    pendingIntent);
        }

        Log.d(TAG, "Alarm kuruldu: " + hour + ":" + String.format("%02d", minute) +
                " (" + targetTime.getTime() + ")");
    }

    /**
     * Bir sonraki günün alarm'ını kurar.
     * AlarmReceiver tarafından bildirim gösterildikten sonra çağrılır.
     */
    public static void scheduleNextDayAlarm(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int hour = prefs.getInt(KEY_NOTIF_HOUR, 9);
        int minute = prefs.getInt(KEY_NOTIF_MINUTE, 0);

        scheduleExactAlarm(context, hour, minute);
    }

    /**
     * Mevcut alarm'ı iptal eder.
     */
    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Alarm iptal edildi");
    }
}
