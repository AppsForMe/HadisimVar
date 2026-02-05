package com.sinan.hadisimvar.ui.settings;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.ui.base.BaseActivity;
import com.sinan.hadisimvar.ui.landing.LandingActivity;
import com.sinan.hadisimvar.utils.NotificationScheduler;
import com.sinan.hadisimvar.utils.ThemeHelper;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private TextView tvNotificationTime;
    private RadioGroup radioGroupThemes, radioGroupFontSize;
    private RadioButton rbSafir, rbMedine, rbGece, rbSahra, rbMermer, rbGunBatimi;
    private RadioButton rbFontSmall, rbFontMedium, rbFontLarge;
    private SharedPreferences prefs;

    // Bildirim izni bekleme değişkenleri
    private int pendingNotificationHour = -1;
    private int pendingNotificationMinute = -1;

    private static final String PREF_NAME = "settings_prefs";
    private static final String KEY_NOTIF_HOUR = "notif_hour";
    private static final String KEY_NOTIF_MINUTE = "notif_minute";
    public static final String KEY_FONT_SIZE = "font_size";

    // Android 13+ için bildirim izni launcher
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // İzin verildi, bildirimi zamanla
                    if (pendingNotificationHour >= 0) {
                        saveTime(pendingNotificationHour, pendingNotificationMinute);
                        updateTimeText(pendingNotificationHour, pendingNotificationMinute);
                        NotificationScheduler.scheduleDaily(this, pendingNotificationHour, pendingNotificationMinute);
                        Toast.makeText(this, "Bildirim saati güncellendi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Bildirim izni verilmedi", Toast.LENGTH_SHORT).show();
                }
                // Reset
                pendingNotificationHour = -1;
                pendingNotificationMinute = -1;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Geri butonu setup
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initViews();
        loadSavedSettings();
        setupListeners();
    }

    private void initViews() {
        tvNotificationTime = findViewById(R.id.tvNotificationTime);
        radioGroupThemes = findViewById(R.id.radioGroupThemes);

        rbSafir = findViewById(R.id.rbSafir);
        rbMedine = findViewById(R.id.rbMedine);
        rbGece = findViewById(R.id.rbGece);
        rbSahra = findViewById(R.id.rbSahra);
        rbMermer = findViewById(R.id.rbMermer);
        rbGunBatimi = findViewById(R.id.rbGunBatimi);

        radioGroupFontSize = findViewById(R.id.radioGroupFontSize);
        rbFontSmall = findViewById(R.id.rbFontSmall);
        rbFontMedium = findViewById(R.id.rbFontMedium);
        rbFontLarge = findViewById(R.id.rbFontLarge);

        CardView cardTime = findViewById(R.id.cardNotificationTime);
        cardTime.setOnClickListener(v -> showTimePicker());
    }

    private void loadSavedSettings() {
        // Time
        int hour = prefs.getInt(KEY_NOTIF_HOUR, 9);
        int minute = prefs.getInt(KEY_NOTIF_MINUTE, 0);
        updateTimeText(hour, minute);

        // Theme - Yeni 6 Tema
        String currentTheme = ThemeHelper.getSelectedTheme(this);
        switch (currentTheme) {
            case ThemeHelper.THEME_SAFIR:
                rbSafir.setChecked(true);
                break;
            case ThemeHelper.THEME_GECE:
                rbGece.setChecked(true);
                break;
            case ThemeHelper.THEME_SAHRA:
                rbSahra.setChecked(true);
                break;
            case ThemeHelper.THEME_MERMER:
                rbMermer.setChecked(true);
                break;
            case ThemeHelper.THEME_GUNBATIMI:
                rbGunBatimi.setChecked(true);
                break;
            case ThemeHelper.THEME_MEDINE:
            default:
                rbMedine.setChecked(true);
                break;
        }

        // Font Size
        float fontSize = prefs.getFloat(KEY_FONT_SIZE, 18f);
        if (fontSize == 16f)
            rbFontSmall.setChecked(true);
        else if (fontSize == 22f)
            rbFontLarge.setChecked(true);
        else
            rbFontMedium.setChecked(true);
    }

    private void setupListeners() {
        radioGroupThemes.setOnCheckedChangeListener((group, checkedId) -> {
            String newTheme = ThemeHelper.THEME_MEDINE; // Varsayılan

            if (checkedId == R.id.rbSafir)
                newTheme = ThemeHelper.THEME_SAFIR;
            else if (checkedId == R.id.rbMedine)
                newTheme = ThemeHelper.THEME_MEDINE;
            else if (checkedId == R.id.rbGece)
                newTheme = ThemeHelper.THEME_GECE;
            else if (checkedId == R.id.rbSahra)
                newTheme = ThemeHelper.THEME_SAHRA;
            else if (checkedId == R.id.rbMermer)
                newTheme = ThemeHelper.THEME_MERMER;
            else if (checkedId == R.id.rbGunBatimi)
                newTheme = ThemeHelper.THEME_GUNBATIMI;

            String currentTheme = ThemeHelper.getSelectedTheme(this);
            if (!currentTheme.equals(newTheme)) {
                ThemeHelper.saveTheme(this, newTheme);
                // Yumuşak geçiş animasyonu ile yeniden başlat
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        radioGroupFontSize.setOnCheckedChangeListener((group, checkedId) -> {
            float size = 18f;
            if (checkedId == R.id.rbFontSmall)
                size = 16f;
            else if (checkedId == R.id.rbFontLarge)
                size = 22f;

            prefs.edit().putFloat(KEY_FONT_SIZE, size).apply();
            Toast.makeText(this, "Yazı boyutu güncellendi", Toast.LENGTH_SHORT).show();
        });
    }

    private void showTimePicker() {
        int hour = prefs.getInt(KEY_NOTIF_HOUR, 9);
        int minute = prefs.getInt(KEY_NOTIF_MINUTE, 0);

        TimePickerDialog picker = new TimePickerDialog(this, (view, h, m) -> {
            // Android 13+ için bildirim izni kontrolü
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // İzin yok, iste
                    pendingNotificationHour = h;
                    pendingNotificationMinute = m;
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    return;
                }
            }
            // İzin var veya Android 12 ve altı
            saveTime(h, m);
            updateTimeText(h, m);
            NotificationScheduler.scheduleDaily(this, h, m);
            Toast.makeText(this, "Bildirim saati güncellendi", Toast.LENGTH_SHORT).show();
        }, hour, minute, true);
        picker.show();
    }

    private void saveTime(int hour, int minute) {
        prefs.edit()
                .putInt(KEY_NOTIF_HOUR, hour)
                .putInt(KEY_NOTIF_MINUTE, minute)
                .apply();
    }

    private void updateTimeText(int hour, int minute) {
        tvNotificationTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
    }

    private void restartApp() {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
