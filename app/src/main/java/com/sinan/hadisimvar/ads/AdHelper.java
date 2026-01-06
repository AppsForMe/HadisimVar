package com.sinan.hadisimvar.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * ═══════════════════════════════════════════════════════════════════
 * 💰 AdMob Reklam Yardımcı Sınıfı
 * ═══════════════════════════════════════════════════════════════════
 * <p>
 * Bu sınıf Banner ve Interstitial reklamların yüklenmesini ve
 * gösterilmesini kolaylaştırır.
 */
public class AdHelper {

    private static final String TAG = "AdHelper";

    private static InterstitialAd mInterstitialAd;
    private static int hadithChangeCount = 0;
    private static boolean isInitialized = false;

    // ═══════════════════════════════════════════════════════════════
    // BAŞLATMA
    // ═══════════════════════════════════════════════════════════════

    /**
     * AdMob SDK'yı başlatır.
     * Application sınıfında veya ilk Activity'de çağrılmalıdır.
     */
    public static void initialize(Context context) {
        if (isInitialized)
            return;

        MobileAds.initialize(context, initializationStatus -> {
            Log.d(TAG, "AdMob SDK başlatıldı");
            isInitialized = true;
        });
    }

    // ═══════════════════════════════════════════════════════════════
    // BANNER REKLAM
    // ═══════════════════════════════════════════════════════════════

    /**
     * Banner reklamı yükler ve gösterir.
     *
     * @param activity    Reklam gösterilecek Activity
     * @param adContainer Banner'ın yerleştirileceği FrameLayout
     */
    public static void loadBannerAd(Activity activity, FrameLayout adContainer) {
        if (adContainer == null) {
            Log.w(TAG, "Banner container null!");
            return;
        }

        AdView adView = new AdView(activity);
        adView.setAdUnitId(AdConfig.getBannerAdId());
        adView.setAdSize(AdSize.BANNER);

        // Eski reklamı temizle
        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Reklamı yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Log.d(TAG, "Banner reklam yükleniyor...");
    }

    /**
     * Adaptive Banner reklamı yükler (ekran genişliğine göre).
     */
    public static void loadAdaptiveBannerAd(Activity activity, FrameLayout adContainer) {
        if (adContainer == null) {
            Log.w(TAG, "Banner container null!");
            return;
        }

        AdView adView = new AdView(activity);
        adView.setAdUnitId(AdConfig.getBannerAdId());

        // Adaptive banner boyutu
        AdSize adSize = getAdaptiveBannerSize(activity, adContainer);
        adView.setAdSize(adSize);

        // Eski reklamı temizle
        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Reklamı yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Log.d(TAG, "Adaptive Banner reklam yükleniyor...");
    }

    private static AdSize getAdaptiveBannerSize(Activity activity, FrameLayout adContainer) {
        // Ekran genişliğini hesapla
        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adContainer.getWidth() / density);

        if (adWidth <= 0) {
            adWidth = (int) (activity.getResources().getDisplayMetrics().widthPixels / density);
        }

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    // ═══════════════════════════════════════════════════════════════
    // INTERSTITIAL (TAM EKRAN) REKLAM
    // ═══════════════════════════════════════════════════════════════

    /**
     * Interstitial reklamı önceden yükler.
     * Activity başladığında çağrılmalıdır.
     */
    public static void loadInterstitialAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, AdConfig.getInterstitialAdId(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "Interstitial reklam yüklendi");

                        // Tam ekran callback
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Interstitial kapatıldı");
                                mInterstitialAd = null;
                                // Yeni reklam yükle
                                loadInterstitialAd(context);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Interstitial gösterilemedi: " + adError.getMessage());
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "Interstitial yüklenemedi: " + loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    /**
     * Hadis değiştiğinde çağrılır.
     * Belirli sayıda değişimden sonra Interstitial gösterir.
     */
    public static void onHadithChanged(Activity activity) {
        hadithChangeCount++;

        if (hadithChangeCount >= AdConfig.INTERSTITIAL_FREQUENCY) {
            showInterstitialAd(activity);
            hadithChangeCount = 0;
        }
    }

    /**
     * Interstitial reklamı gösterir (eğer hazırsa).
     */
    public static void showInterstitialAd(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            Log.d(TAG, "Interstitial gösteriliyor");
        } else {
            Log.d(TAG, "Interstitial henüz hazır değil");
        }
    }

    /**
     * Interstitial hazır mı?
     */
    public static boolean isInterstitialReady() {
        return mInterstitialAd != null;
    }

    // ═══════════════════════════════════════════════════════════════
    // TEMİZLİK
    // ═══════════════════════════════════════════════════════════════

    /**
     * Hadith sayacını sıfırlar
     */
    public static void resetHadithCounter() {
        hadithChangeCount = 0;
    }
}
