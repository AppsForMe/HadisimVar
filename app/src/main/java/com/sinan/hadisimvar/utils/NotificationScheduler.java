package com.sinan.hadisimvar.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.sinan.hadisimvar.workers.DailyHadithWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    private static final String WORK_NAME = "DailyHadithWork";

    public static void scheduleDaily(Context context, int hour, int minute) {
        WorkManager workManager = WorkManager.getInstance(context);

        // Şimdiki zaman
        Calendar currentDate = Calendar.getInstance();

        // Hedef zaman
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR_OF_DAY, hour);
        dueDate.set(Calendar.MINUTE, minute);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            // Eğer saat geçmişse yarına ayarla
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        // Kısıtlamalar (gerekirse)
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
                DailyHadithWorker.class,
                24,
                TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Öncekisini iptal et ve yenisini koy
                dailyWorkRequest);
    }
}
