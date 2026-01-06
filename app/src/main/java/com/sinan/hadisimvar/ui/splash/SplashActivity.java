package com.sinan.hadisimvar.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.sinan.hadisimvar.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Veritabanı seed işlemi HadisApp (Application) veya
        // ilk Repository oluşturulduğunda otomatik yapılır.
        // Burada ayrıca tetiklemeye gerek yok.

        // 2 saniye sonra ana sayfaya geç
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, com.sinan.hadisimvar.ui.landing.LandingActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
