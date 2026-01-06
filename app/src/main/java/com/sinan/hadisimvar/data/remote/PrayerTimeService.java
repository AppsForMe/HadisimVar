package com.sinan.hadisimvar.data.remote;

import com.sinan.hadisimvar.data.remote.model.PrayerTimesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrayerTimeService {
    // Aladhan API: https://api.aladhan.com/v1/timingsByCity
    // Example:
    // https://api.aladhan.com/v1/timingsByCity?city=Istanbul&country=Turkey&method=13

    @GET("timingsByCity")
    Call<PrayerTimesResponse> getPrayerTimes(
            @Query("city") String city,
            @Query("country") String country,
            @Query("method") int method // 13: Diyanet İşleri Başkanlığı
    );
}
