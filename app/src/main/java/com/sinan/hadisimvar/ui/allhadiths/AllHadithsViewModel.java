package com.sinan.hadisimvar.ui.allhadiths;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.data.repository.HadithRepository;
import com.sinan.hadisimvar.ui.base.BaseViewModel;

import java.util.List;

public class AllHadithsViewModel extends BaseViewModel {

    private final HadithRepository repository;
    private final LiveData<List<Hadith>> allHadiths;
    private final androidx.lifecycle.MediatorLiveData<List<Hadith>> filteredHadiths = new androidx.lifecycle.MediatorLiveData<>();
    private final androidx.lifecycle.MutableLiveData<String> currentFilter = new androidx.lifecycle.MutableLiveData<>(
            "Tümü");

    public AllHadithsViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
        allHadiths = repository.getAllHadiths();

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

        List<Hadith> result = new java.util.ArrayList<>();
        for (Hadith h : hadiths) {
            // Basit contains araması yapıyoruz. Hem source hem category hem authenticity
            // alanlarına bakabiliriz.
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
            // Kaynak adında ara (Örn: Buhari, Müslim) veya Konularda ara
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
            java.util.Set<String> topics = new java.util.HashSet<>();
            topics.add("Tümü"); // Default
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
            java.util.List<String> sorted = new java.util.ArrayList<>(topics);
            java.util.Collections.sort(sorted);
            // Move "Tümü" to top
            sorted.remove("Tümü");
            sorted.add(0, "Tümü");
            return sorted;
        });
    }

    public LiveData<List<Hadith>> getFilteredHadiths() {
        return filteredHadiths;
    }

    public void setFilter(String filter) {
        currentFilter.setValue(filter);
    }

    // Deprecated but keeping for compatibility if needed elsewhere
    public LiveData<List<Hadith>> getAllHadiths() {
        return allHadiths;
    }
}
