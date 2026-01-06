package com.sinan.hadisimvar.ui.favorites;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.data.repository.HadithRepository;
import com.sinan.hadisimvar.ui.base.BaseViewModel;

import java.util.List;

public class FavoritesViewModel extends BaseViewModel {

    private final HadithRepository repository;
    private final LiveData<List<Hadith>> favoriteHadiths;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
        favoriteHadiths = repository.getFavoriteHadiths();
    }

    public LiveData<List<Hadith>> getFavoriteHadiths() {
        return favoriteHadiths;
    }

    public void removeFromFavorites(Hadith hadith) {
        hadith.setFavorite(false);
        repository.updateHadith(hadith);
    }
}
