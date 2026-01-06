package com.sinan.hadisimvar.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.data.repository.HadithRepository;
import com.sinan.hadisimvar.ui.base.BaseViewModel;

/**
 * HadithDetailActivity için ViewModel.
 * ID ile doğrudan hadis getirme yöntemi sunar.
 */
public class HadithDetailViewModel extends BaseViewModel {

    private final HadithRepository repository;

    public HadithDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new HadithRepository(application);
    }

    /**
     * ID ile hadis getirir.
     *
     * @param id Hadis ID'si
     * @return LiveData olarak hadis
     */
    public LiveData<Hadith> getHadithById(int id) {
        return repository.getHadithById(id);
    }
}
