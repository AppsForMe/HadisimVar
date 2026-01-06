package com.sinan.hadisimvar.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.AppDatabase;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.ui.landing.LandingActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HadithWidget extends AppWidgetProvider {

    private static final String ACTION_REFRESH = "com.sinan.hadisimvar.ACTION_REFRESH";

    // Singleton ExecutorService - memory leak önleme
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_hadith);

        // Click on widget to open app
        Intent intent = new Intent(context, LandingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // Refresh button
        Intent refreshIntent = new Intent(context, HadithWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btnRefresh, refreshPendingIntent);

        // Load data in background
        loadRandomHadith(context, views, appWidgetManager, appWidgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HadithWidget.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    private void loadRandomHadith(Context context, RemoteViews views, AppWidgetManager appWidgetManager,
                                  int appWidgetId) {
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);

            // DAO'ya eklediğimiz senkron metodu kullanıyoruz
            java.util.List<Hadith> all = db.hadithDao().getAllHadithsSync();

            if (all != null && !all.isEmpty()) {
                Hadith randomHadith = all.get(new java.util.Random().nextInt(all.size()));
                handler.post(() -> {
                    views.setTextViewText(R.id.tvWidgetContent, randomHadith.content);
                    views.setTextViewText(R.id.tvWidgetSource, "- " + randomHadith.source);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                });
            } else {
                handler.post(() -> {
                    views.setTextViewText(R.id.tvWidgetContent, "Hadis bulunamadı.");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                });
            }
        });
    }
}
