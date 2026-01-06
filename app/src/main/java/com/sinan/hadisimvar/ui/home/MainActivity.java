package com.sinan.hadisimvar.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.databinding.ActivityMainBinding;
import com.sinan.hadisimvar.ui.favorites.FavoritesActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.sinan.hadisimvar.ui.base.BaseActivity;

// AdMob imports
import com.sinan.hadisimvar.ads.AdHelper;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private HomeViewModel viewModel;
    private Hadith currentHadith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupUI();
        observeData();
        checkLocationPermission();

        // ═══════════════════════════════════════════════════════════════
        // 💰 ADMOB BAŞLATMA
        // ═══════════════════════════════════════════════════════════════
        AdHelper.initialize(this);
        AdHelper.loadBannerAd(this, binding.adBannerContainer);
        AdHelper.loadInterstitialAd(this);
    }

    private void setupUI() {
        // Miladi tarih ayarla
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, EEEE", new Locale("tr"));
        binding.tvDate.setText(sdf.format(new Date()));

        // Hicri tarih hesapla ve göster
        setHijriDate();

        binding.btnFavorite.setOnClickListener(v -> {
            if (currentHadith != null) {
                boolean newState = !currentHadith.isFavorite();
                currentHadith.setFavorite(newState);
                viewModel.updateHadith(currentHadith);
                updateFavoriteIcon(newState);
                Toast.makeText(this, newState ? "Favorilere eklendi" : "Favorilerden çıkarıldı", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        binding.btnShare.setOnClickListener(v -> {
            if (currentHadith != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentHadith.getContent() + "\n\n- " + currentHadith.getSource()
                        + "\n(Hadisim Var Uygulaması)");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Hadisi Paylaş"));
            }
        });

        binding.fabFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        binding.btnAllHadiths.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.sinan.hadisimvar.ui.allhadiths.AllHadithsActivity.class);
            startActivity(intent);
        });

        binding.getRoot().findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.sinan.hadisimvar.ui.settings.SettingsActivity.class);
            startActivity(intent);
        });

        binding.getRoot().findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

        // Daily Hadith Card Click - Popup
        binding.cardDailyHadith.setOnClickListener(v -> {
            if (currentHadith != null) {
                showHadithPopup(currentHadith);
            }
        });
    }

    private void showHadithPopup(Hadith hadith) {
        StringBuilder msg = new StringBuilder();
        msg.append(hadith.getContent()).append("\n\n");
        msg.append("- ").append(hadith.getSource()).append("\n\n");

        if (hadith.explanation != null && !hadith.explanation.isEmpty()) {
            msg.append("Şerh / Açıklama:\n").append(hadith.explanation).append("\n\n");
        }

        if (hadith.topics != null && !hadith.topics.isEmpty()) {
            msg.append("Konular: ").append(hadith.topics);
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Günün Hadisi")
                .setMessage(msg.toString())
                .setPositiveButton("Paylaş", (dialog, which) -> binding.btnShare.performClick())
                .setNeutralButton("Kapat", null)
                .setNegativeButton("Detaylar", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this,
                            com.sinan.hadisimvar.ui.detail.HadithDetailActivity.class);
                    intent.putExtra(com.sinan.hadisimvar.ui.detail.HadithDetailActivity.EXTRA_HADITH_ID,
                            hadith.getId());
                    startActivity(intent);
                })
                .show();
    }

    private void observeData() {
        viewModel.getRandomHadith().observe(this, hadith -> {
            if (hadith != null) {
                currentHadith = hadith;
                binding.tvHadithContent.setText(hadith.getContent());
                binding.tvHadithSource.setText(hadith.getSource());
                updateFavoriteIcon(hadith.isFavorite());

                // 💰 Hadis değiştiğinde interstitial kontrolü
                AdHelper.onHadithChanged(this);
            }
        });

        viewModel.getPrayerTimings().observe(this, timings -> {
            if (timings != null) {
                binding.tvFajr.setText(timings.fajr);
                binding.tvSunrise.setText(timings.sunrise);
                binding.tvDhuhr.setText(timings.dhuhr);
                binding.tvAsr.setText(timings.asr);
                binding.tvMaghrib.setText(timings.maghrib);
                binding.tvIsha.setText(timings.isha);
            }
        });

        viewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                // Hata kodunu detaylı görebilmek için Alert Dialog
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Hata")
                        .setMessage(errorMsg)
                        .setPositiveButton("Tamam", null)
                        .show();
            }
        });
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            binding.btnFavorite.setIconResource(R.drawable.ic_favorite);
            binding.btnFavorite.setText("Favoride");
        } else {
            binding.btnFavorite.setIconResource(R.drawable.ic_favorite_border);
            binding.btnFavorite.setText("");
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getLocationAndFetchPrayerTimes();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLocationAndFetchPrayerTimes();
                } else {
                    Toast.makeText(this, "Namaz vakitleri için konum izni gerekli.", Toast.LENGTH_LONG).show();
                    // Varsayılan olarak Istanbul'u çekelim izin verilmezse
                    binding.tvPrayerCity.setText("İstanbul");
                    viewModel.fetchPrayerTimes("Istanbul", "Turkey");
                }
            });

    private void getLocationAndFetchPrayerTimes() {
        LocationManager locationManager = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null) {
            final Location finalLoc = location;
            new Thread(() -> {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(finalLoc.getLatitude(), finalLoc.getLongitude(),
                            1);
                    if (addresses != null && !addresses.isEmpty()) {
                        String city = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();

                        if (city == null)
                            city = addresses.get(0).getSubAdminArea();
                        if (city == null)
                            city = addresses.get(0).getLocality();

                        if (city != null) {
                            String finalCity = city;
                            String finalCountry = country;
                            runOnUiThread(() -> {
                                binding.tvPrayerCity.setText(finalCity);
                                viewModel.fetchPrayerTimes(finalCity, finalCountry);
                            });
                        } else {
                            runOnUiThread(() -> {
                                binding.tvPrayerCity.setText("İstanbul");
                                viewModel.fetchPrayerTimes("Istanbul", "Turkey");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            binding.tvPrayerCity.setText("İstanbul");
                            viewModel.fetchPrayerTimes("Istanbul", "Turkey");
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        binding.tvPrayerCity.setText("İstanbul");
                        viewModel.fetchPrayerTimes("Istanbul", "Turkey");
                    });
                }
            }).start();
        } else {
            Toast.makeText(this, "Konum alınamadı, İstanbul varsayılan yapıldı.", Toast.LENGTH_LONG).show();
            binding.tvPrayerCity.setText("İstanbul");
            viewModel.fetchPrayerTimes("Istanbul", "Turkey");
        }
    }

    /**
     * Hicri tarihi hesaplar ve gösterir.
     * Basit bir Umm al-Qura yaklaşımı kullanılır.
     */
    private void setHijriDate() {
        try {
            // Android 8.0+ için HijrahDate kullanılabilir, ancak minSdk 24 olduğu için
            // basit bir hesaplama yöntemi kullanıyoruz
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            int month = cal.get(java.util.Calendar.MONTH) + 1;
            int year = cal.get(java.util.Calendar.YEAR);

            // Miladi -> Hicri dönüşüm (basitleştirilmiş algoritma)
            int[] hijri = gregorianToHijri(year, month, day);

            String[] hijriMonths = {
                    "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
                    "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
                    "Ramazan", "Şevval", "Zilkade", "Zilhicce"
            };

            String hijriDateText = hijri[2] + " " + hijriMonths[hijri[1] - 1] + " " + hijri[0];
            binding.tvHijriDate.setText(hijriDateText);
        } catch (Exception e) {
            // Hata durumunda boş bırak
            binding.tvHijriDate.setText("");
        }
    }

    /**
     * Miladi tarihi Hicri tarihe dönüştürür.
     *
     * @return int[] {yıl, ay, gün}
     */
    private int[] gregorianToHijri(int year, int month, int day) {
        // Julian Day Number hesaplama
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;
        int jd = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;

        // Hicri takvim dönüşümü (Umm al-Qura yaklaşımı)
        int l = jd - 1948440 + 10632;
        int n = (l - 1) / 10631;
        l = l - 10631 * n + 354;
        int j = ((10985 - l) / 5316) * ((50 * l) / 17719) + (l / 5670) * ((43 * l) / 15238);
        l = l - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29;
        int hijriMonth = (24 * l) / 709;
        int hijriDay = l - (709 * hijriMonth) / 24;
        int hijriYear = 30 * n + j - 30;

        return new int[]{hijriYear, hijriMonth, hijriDay};
    }
}
