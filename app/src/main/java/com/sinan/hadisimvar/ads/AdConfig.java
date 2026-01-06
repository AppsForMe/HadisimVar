package com.sinan.hadisimvar.ads;

/**
 * ═══════════════════════════════════════════════════════════════════
 * 💰 AdMob Reklam ID Yapılandırması
 * ═══════════════════════════════════════════════════════════════════
 * <p>
 * Bu sınıf tüm AdMob reklam ID'lerini merkezi olarak yönetir.
 * <p>
 * NOT: Test modunda Google'ın test ID'leri kullanılabilir.
 * Production'a geçmeden önce gerçek ID'lerin doğru olduğundan emin olun.
 */
public class AdConfig {

    // ═══════════════════════════════════════════════════════════════
    // GERÇEK ADMOB ID'LERİ (Production)
    // ═══════════════════════════════════════════════════════════════

    /**
     * AdMob Application ID
     */
    public static final String APP_ID = "ca-app-pub-6888537824609825~1672302521";

    /**
     * Banner Reklam ID - Ana Sayfa
     */
    public static final String BANNER_AD_UNIT_ID = "ca-app-pub-6888537824609825/4106894176";

    /**
     * Interstitial (Geçiş) Reklam ID
     */
    public static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-6888537824609825/6488163860";

    // ═══════════════════════════════════════════════════════════════
    // TEST ID'LERİ (Development - Google'ın resmi test ID'leri)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Test Banner ID - Geliştirme sırasında kullanın
     */
    public static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";

    /**
     * Test Interstitial ID - Geliştirme sırasında kullanın
     */
    public static final String TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";

    // ═══════════════════════════════════════════════════════════════
    // YAPILANDIRMA
    // ═══════════════════════════════════════════════════════════════

    /**
     * Test modu aktif mi?
     * <p>
     * TRUE = Google test reklamları gösterilir (geliştirme için)
     * FALSE = Gerçek reklamlar gösterilir (yayına almadan önce)
     * <p>
     * ⚠️ UYARI: Play Store'a yüklemeden önce bunu FALSE yapın!
     */
    public static final boolean USE_TEST_ADS = false;

    /**
     * Interstitial reklam gösterme sıklığı
     * Kaç hadis değişiminde bir tam ekran reklam gösterilsin?
     */
    public static final int INTERSTITIAL_FREQUENCY = 5;

    // ═══════════════════════════════════════════════════════════════
    // YARDIMCI METODLAR
    // ═══════════════════════════════════════════════════════════════

    /**
     * Kullanılacak Banner ID'sini döndürür
     */
    public static String getBannerAdId() {
        return USE_TEST_ADS ? TEST_BANNER_ID : BANNER_AD_UNIT_ID;
    }

    /**
     * Kullanılacak Interstitial ID'sini döndürür
     */
    public static String getInterstitialAdId() {
        return USE_TEST_ADS ? TEST_INTERSTITIAL_ID : INTERSTITIAL_AD_UNIT_ID;
    }
}
