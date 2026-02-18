package com.sinan.hadisimvar.ui.allhadiths;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.data.repository.HadithRepository;
import com.sinan.hadisimvar.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllHadithsViewModel extends BaseViewModel {

    private final HadithRepository repository;
    private final LiveData<List<Hadith>> allHadiths;
    private final MediatorLiveData<List<Hadith>> filteredHadiths = new MediatorLiveData<>();
    private final MutableLiveData<String> currentFilter = new MutableLiveData<>("Tümü");

    // Loading state - seed tamamlanana kadar true
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    // Seed observer referansı - memory leak önleme
    private Observer<Boolean> seedingObserver;

    public AllHadithsViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
        allHadiths = repository.getAllHadiths();

        // Seed tamamlanana kadar bekle
        seedingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isComplete) {
                if (Boolean.TRUE.equals(isComplete)) {
                    repository.isSeedingComplete().removeObserver(this);
                    seedingObserver = null;
                    isLoading.setValue(false);

                    // Seed tamamlandıktan sonra filter source'ları ekle
                    setupFilterSources();
                }
            }
        };
        repository.isSeedingComplete().observeForever(seedingObserver);
    }

    private void setupFilterSources() {
        filteredHadiths.addSource(allHadiths, hadiths -> filterList(hadiths, currentFilter.getValue()));
        filteredHadiths.addSource(currentFilter, filter -> filterList(allHadiths.getValue(), filter));
    }

    private void filterList(List<Hadith> hadiths, String filter) {
        if (hadiths == null) {
            filteredHadiths.setValue(null);
            return;
        }

        if (filter == null || filter.equals("Tümü")) {
            filteredHadiths.setValue(hadiths);
            return;
        }

        List<Hadith> result = new ArrayList<>();
        for (Hadith h : hadiths) {
            boolean matches = false;

            // Sahih filtresi
            if (filter.equals("Sahih Hadisler")) {
                if ("Sahih".equalsIgnoreCase(h.authenticity))
                    matches = true;
            }
            // Kütüb-i Sitte
            else if (filter.equals("Kütüb-i Sitte")) {
                if (h.sourceCategory != null && h.sourceCategory.contains("Kütüb-i Sitte"))
                    matches = true;
            }
            // Kaynak adında ara veya Konularda ara
            else {
                boolean sourceMatch = h.source != null && h.source.contains(filter);
                boolean topicMatch = h.topics != null && h.topics.contains(filter);
                if (sourceMatch || topicMatch)
                    matches = true;
            }

            if (matches)
                result.add(h);
        }
        filteredHadiths.setValue(result);
    }

    public LiveData<List<String>> getUniqueTopics() {
        return androidx.lifecycle.Transformations.map(repository.getAllHadiths(), hadiths -> {
            Set<String> topics = new HashSet<>();
            topics.add("Tümü");
            if (hadiths != null) {
                for (Hadith h : hadiths) {
                    if (h.topics != null && !h.topics.isEmpty()) {
                        String[] split = h.topics.split(",");
                        for (String s : split) {
                            topics.add(s.trim());
                        }
                    }
                }
            }
            List<String> sorted = new ArrayList<>(topics);
            Collections.sort(sorted);
            sorted.remove("Tümü");
            sorted.add(0, "Tümü");
            return sorted;
        });
    }

    public LiveData<List<Hadith>> getFilteredHadiths() {
        return filteredHadiths;
    }

    /**
     * Loading durumunu döndürür. Seed tamamlanana kadar true.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setFilter(String filter) {
        currentFilter.setValue(filter);
    }

    public LiveData<List<Hadith>> getAllHadiths() {
        return allHadiths;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Observer'ı temizle
        if (seedingObserver != null) {
            repository.isSeedingComplete().removeObserver(seedingObserver);
            seedingObserver = null;
        }
    }
}
