package com.sinan.hadisimvar.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sinan.hadisimvar.data.local.entity.Hadith;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface HadithDao {

    @Insert
    void insertAll(List<Hadith> hadiths);

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    LiveData<Hadith> getRandomHadith();

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    Hadith getRandomHadithSync();

    @Query("SELECT * FROM hadiths WHERE id = :id")
    LiveData<Hadith> getHadithById(int id);

    @Query("SELECT * FROM hadiths")
    LiveData<List<Hadith>> getAllHadiths();

    @Query("SELECT * FROM hadiths")
    List<Hadith> getAllHadithsSync();

    @Query("SELECT * FROM hadiths WHERE is_favorite = 1 ORDER BY favorite_date DESC")
    LiveData<List<Hadith>> getFavoriteHadiths();

    @Update
    void updateHadith(Hadith hadith);

    @Query("SELECT COUNT(*) FROM hadiths")
    int getHadithCount();
}
