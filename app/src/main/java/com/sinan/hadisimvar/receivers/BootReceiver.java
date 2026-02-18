package com.sinan.hadisimvar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.sinan.hadisimvar.utils.NotificationScheduler;

/**
 * Cihaz yeniden başladığında alarm'ı yeniden kuran BroadcastReceiver.
 * AlarmManager alarm'ları cihaz restart'ında silinir, bu receiver onları geri
 * yükler.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String PREF_NAME = "settings_prefs";
    private static final String KEY_NOTIF_HOUR = "notif_hour";
    private static final String KEY_NOTIF_MINUTE = "notif_minute";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot tamamlandı - Alarm yeniden kuruluyor");

            // Kaydedilmiş bildirim saatini oku
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            int hour = prefs.getInt(KEY_NOTIF_HOUR, 9);
            int minute = prefs.getInt(KEY_NOTIF_MINUTE, 0);

            // Alarm'ı yeniden kur
            NotificationScheduler.scheduleExactAlarm(context, hour, minute);
            Log.d(TAG, "Alarm yeniden kuruldu: " + hour + ":" + minute);
        }
    }
}
