package com.sinan.hadisimvar.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sinan.hadisimvar.data.local.AppDatabase;
import com.sinan.hadisimvar.data.local.dao.HadithDao;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HadithRepository {

    private static final String TAG = "HadithRepository";

    private final HadithDao hadithDao;
    private final LiveData<Hadith> randomHadith;
    private final ExecutorService executorService;
    private final Context context;

    // Seed durumunu takip etmek için
    private final MutableLiveData<Boolean> seedingComplete = new MutableLiveData<>(false);

    public HadithRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        hadithDao = db.hadithDao();
        randomHadith = hadithDao.getRandomHadith();
        executorService = Executors.newSingleThreadExecutor();
        this.context = application.getApplicationContext();

        // Veritabanı boşsa doldur
        checkAndSeedDatabase();
    }

    private void checkAndSeedDatabase() {
        executorService.execute(() -> {
            try {
                int dbCount = hadithDao.getHadithCount();
                // Veritabanı boşsa veya versiyon güncellendiyse (destruktif migration sonrası
                // boşalır) yeniden doldur.
                if (dbCount == 0) {
                    seedDatabase();
                } else {
                    // Var olan mantık: Yeni eklenenleri append et
                    String jsonString = loadJSONFromAsset();
                    if (jsonString != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Hadith>>() {
                        }.getType();
                        List<Hadith> allHadiths = gson.fromJson(jsonString, listType);

                        if (allHadiths != null && allHadiths.size() > dbCount) {
                            List<Hadith> newHadiths = allHadiths.subList(dbCount, allHadiths.size());
                            hadithDao.insertAll(newHadiths);
                            Log.d(TAG, "Yeni " + newHadiths.size() + " hadis eklendi.");
                        }
                    }
                }
                seedingComplete.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "Veritabanı seed hatası: " + e.getMessage(), e);
                seedingComplete.postValue(true); // Hata olsa bile devam et
            }
        });
    }

    private void seedDatabase() {
        String jsonString = loadJSONFromAsset();
        if (jsonString != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Hadith>>() {
            }.getType();
            List<Hadith> hadiths = gson.fromJson(jsonString, listType);
            if (hadiths != null && !hadiths.isEmpty()) {
                hadithDao.insertAll(hadiths);
                Log.d(TAG, hadiths.size() + " hadis veritabanına eklendi.");
            }
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try (InputStream is = context.getAssets().open("hadiths.json")) {
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            if (bytesRead > 0) {
                json = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            Log.e(TAG, "JSON dosyası okunamadı: " + ex.getMessage(), ex);
            return null;
        }
        return json;
    }

    public LiveData<Hadith> getRandomHadith() {
        return randomHadith;
    }

    public LiveData<List<Hadith>> getAllHadiths() {
        return hadithDao.getAllHadiths();
    }

    public LiveData<List<Hadith>> getFavoriteHadiths() {
        return hadithDao.getFavoriteHadiths();
    }

    public void updateHadith(Hadith hadith) {
        executorService.execute(() -> hadithDao.updateHadith(hadith));
    }

    public LiveData<Hadith> getHadithById(int id) {
        return hadithDao.getHadithById(id);
    }

    public Hadith getRandomHadithSync() {
        return hadithDao.getRandomHadithSync();
    }

    /**
     * Veritabanı seed işleminin tamamlanıp tamamlanmadığını gözlemler.
     */
    public LiveData<Boolean> isSeedingComplete() {
        return seedingComplete;
    }

    /**
     * ExecutorService'i kapatır. Activity/Fragment onDestroy'da çağrılmalı.
     * Memory leak'i önler.
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
