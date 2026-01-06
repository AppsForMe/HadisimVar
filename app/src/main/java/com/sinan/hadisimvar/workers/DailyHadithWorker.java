package com.sinan.hadisimvar.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sinan.hadisimvar.data.local.AppDatabase;
import com.sinan.hadisimvar.data.local.dao.HadithDao;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.utils.NotificationHelper;

public class DailyHadithWorker extends Worker {

    public DailyHadithWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
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
        }
        return Result.success();
    }
}
