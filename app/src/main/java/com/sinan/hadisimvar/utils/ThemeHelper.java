package com.sinan.hadisimvar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sinan.hadisimvar.R;

public class ThemeHelper {

    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    // Tema sabitleri - Yeni 6 Tema
    public static final String THEME_SAFIR = "safir"; // 💎 Saf Safir
    public static final String THEME_MEDINE = "medine"; // 🌿 Medine Yeşili
    public static final String THEME_GECE = "gece"; // 🌙 Gece Okuması
    public static final String THEME_SAHRA = "sahra"; // 🏜️ Sahra Kumları
    public static final String THEME_MERMER = "mermer"; // 🏛️ Asil Mermer
    public static final String THEME_GUNBATIMI = "gunbatimi"; // 🌅 Gün Batımı

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, THEME_MEDINE); // Varsayılan: Medine Yeşili

        switch (theme) {
            case THEME_SAFIR:
                context.setTheme(R.style.Theme_HadisimVar_Safir);
                break;
            case THEME_GECE:
                context.setTheme(R.style.Theme_HadisimVar_Gece);
                break;
            case THEME_SAHRA:
                context.setTheme(R.style.Theme_HadisimVar_Sahra);
                break;
            case THEME_MERMER:
                context.setTheme(R.style.Theme_HadisimVar_Mermer);
                break;
            case THEME_GUNBATIMI:
                context.setTheme(R.style.Theme_HadisimVar_GunBatimi);
                break;
            case THEME_MEDINE:
            default:
                context.setTheme(R.style.Theme_HadisimVar_Medine);
                break;
        }
    }

    public static void saveTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public static String getSelectedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_THEME, THEME_MEDINE);
    }
}
