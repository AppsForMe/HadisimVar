package com.sinan.hadisimvar.ui.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.data.remote.model.Timings;
import com.sinan.hadisimvar.data.repository.HadithRepository;
import com.sinan.hadisimvar.data.repository.PrayerTimeRepository;
import com.sinan.hadisimvar.ui.base.BaseViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends BaseViewModel {

    private static final String TAG = "HomeViewModel";
    private static final String PREFS_NAME = "daily_hadith_prefs";
    private static final String KEY_DATE = "date";
    private static final String KEY_HADITH_ID = "hadith_id";

    private final HadithRepository hadithRepository;
    private final PrayerTimeRepository prayerTimeRepository;
    private final LiveData<Hadith> dailyHadith;
    private final MutableLiveData<Integer> dailyHadithId = new MutableLiveData<>();
    private final LiveData<Timings> prayerTimings;
    private final LiveData<String> error;
    private final ExecutorService executorService;
    private final SharedPreferences prefs;

    // Observer referansı - memory leak önleme için
    private Observer<Boolean> seedingObserver;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        hadithRepository = new HadithRepository(application);
        prayerTimeRepository = new PrayerTimeRepository();
        executorService = Executors.newSingleThreadExecutor();
        prefs = application.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);

        dailyHadith = androidx.lifecycle.Transformations.switchMap(dailyHadithId,
                id -> hadithRepository.getHadithById(id));

        // Seed tamamlandığında günlük hadisi yükle
        seedingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isComplete) {
                if (Boolean.TRUE.equals(isComplete)) {
                    hadithRepository.isSeedingComplete().removeObserver(this);
                    seedingObserver = null; // Referansı temizle
                    loadDailyHadith();
                }
            }
        };
        hadithRepository.isSeedingComplete().observeForever(seedingObserver);

        prayerTimings = prayerTimeRepository.getPrayerTimings();
        error = prayerTimeRepository.getError();
    }

    private void loadDailyHadith() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        String storedDate = prefs.getString(KEY_DATE, "");
        int storedId = prefs.getInt(KEY_HADITH_ID, -1);

        if (today.equals(storedDate) && storedId != -1) {
            // Bugün için zaten bir hadis seçilmiş
            dailyHadithId.setValue(storedId);
            Log.d(TAG, "Kayıtlı günlük hadis yükleniyor: " + storedId);
        } else {
            // Yeni gün, yeni hadis seç
            executorService.execute(() -> {
                try {
                    Hadith random = hadithRepository.getRandomHadithSync();
                    if (random != null) {
                        prefs.edit()
                                .putString(KEY_DATE, today)
                                .putInt(KEY_HADITH_ID, random.getId())
                                .apply();
                        dailyHadithId.postValue(random.getId());
                        Log.d(TAG, "Yeni günlük hadis seçildi: " + random.getId());
                    } else {
                        Log.w(TAG, "Veritabanında hadis bulunamadı");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Günlük hadis yüklenirken hata: " + e.getMessage(), e);
                }
            });
        }
    }

    public LiveData<Hadith> getRandomHadith() {
        return dailyHadith;
    }

    public LiveData<Timings> getPrayerTimings() {
        return prayerTimings;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void updateHadith(Hadith hadith) {
        hadithRepository.updateHadith(hadith);
    }

    public void fetchPrayerTimes(String city, String country) {
        prayerTimeRepository.fetchPrayerTimes(city, country);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Observer'ı temizle (henüz kaldırılmadıysa)
        if (seedingObserver != null) {
            hadithRepository.isSeedingComplete().removeObserver(seedingObserver);
            seedingObserver = null;
        }
        // ExecutorService'i kapat
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
