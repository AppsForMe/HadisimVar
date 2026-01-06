package com.sinan.hadisimvar.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinan.hadisimvar.utils.ThemeHelper;

public class BaseActivity extends AppCompatActivity {

    private String appliedTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Tema uygula ve kaydet
        appliedTheme = ThemeHelper.getSelectedTheme(this);
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tema değişiklik kontrolü - farklı temada ise aktiviteyi yeniden başlat
        String currentTheme = ThemeHelper.getSelectedTheme(this);
        if (appliedTheme != null && !appliedTheme.equals(currentTheme)) {
            appliedTheme = currentTheme;
            recreate();
        }
    }
}
