package com.sinan.hadisimvar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sinan.hadisimvar.data.local.AppDatabase;
import com.sinan.hadisimvar.data.local.dao.HadithDao;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.utils.NotificationHelper;
import com.sinan.hadisimvar.utils.NotificationScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AlarmManager tarafından tetiklenen BroadcastReceiver.
 * Günlük hadis bildirimini gösterir ve bir sonraki günün alarmını kurar.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm tetiklendi - Hadis bildirimi gösteriliyor");

        // Arka plan thread'inde veritabanı işlemi yap
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                HadithDao dao = AppDatabase.getDatabase(context).hadithDao();
                Hadith hadith = dao.getRandomHadithSync();

                if (hadith != null) {
                    NotificationHelper.createNotificationChannels(context);
                    NotificationHelper.showNotification(
                            context,
                            "Günün Hadisi",
                            hadith.getContent(),
                            NotificationHelper.CHANNEL_ID_DAILY,
                            1);
                    Log.d(TAG, "Bildirim gösterildi: " + hadith.getId());
                } else {
                    Log.w(TAG, "Veritabanında hadis bulunamadı");
                }
            } catch (Exception e) {
                Log.e(TAG, "Bildirim gösterilirken hata: " + e.getMessage(), e);
            }
        });

        // Bir sonraki günün alarmını kur
        NotificationScheduler.scheduleNextDayAlarm(context);
    }
}
