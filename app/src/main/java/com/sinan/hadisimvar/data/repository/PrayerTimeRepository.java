package com.sinan.hadisimvar.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sinan.hadisimvar.data.remote.RetrofitClient;
import com.sinan.hadisimvar.data.remote.model.PrayerTimesResponse;
import com.sinan.hadisimvar.data.remote.model.Timings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrayerTimeRepository {

    private MutableLiveData<Timings> prayerTimings = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public void fetchPrayerTimes(String city, String country) {
        // Method 13: Diyanet
        RetrofitClient.getService().getPrayerTimes(city, country, 13).enqueue(new Callback<PrayerTimesResponse>() {
            @Override
            public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    prayerTimings.setValue(response.body().data.timings);
                } else {
                    error.setValue("Veri alınamadı: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                error.setValue("Bağlantı hatası: " + t.getMessage());
            }
        });
    }

    public LiveData<Timings> getPrayerTimings() {
        return prayerTimings;
    }

    public LiveData<String> getError() {
        return error;
    }
}
