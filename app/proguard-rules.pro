# ═══════════════════════════════════════════════════════════════════
# ProGuard Kuralları - HadisimVar
# ═══════════════════════════════════════════════════════════════════

# ─────────────────────────────────────────────────────────────────────
# GENEL AYARLAR
# ─────────────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ─────────────────────────────────────────────────────────────────────
# RETROFIT
# ─────────────────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ─────────────────────────────────────────────────────────────────────
# GSON - KRİTİK: JSON parse için gerekli
# ─────────────────────────────────────────────────────────────────────
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# TypeToken için gerekli
-keep class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keepclassmembers class com.google.gson.reflect.TypeToken {
    *;
}

# ─────────────────────────────────────────────────────────────────────
# UYGULAMA VERİ SINIFLARINI KORU - TÜM PAKETLER
# ─────────────────────────────────────────────────────────────────────
# Model sınıfları
-keep class com.sinan.hadisimvar.data.** { *; }
-keepclassmembers class com.sinan.hadisimvar.data.** { *; }

# Entity sınıfları (Hadith vb.) - GSON ve Room için kritik
-keep class com.sinan.hadisimvar.data.local.entity.** { 
    *;
    <init>(...);
}
-keepclassmembers class com.sinan.hadisimvar.data.local.entity.** {
    *;
}

# Remote model sınıfları (Timings vb.)
-keep class com.sinan.hadisimvar.data.remote.model.** {
    *;
    <init>(...);
}
-keepclassmembers class com.sinan.hadisimvar.data.remote.model.** {
    *;
}

# Repository sınıfları
-keep class com.sinan.hadisimvar.data.repository.** { *; }

# DAO arayüzleri
-keep interface com.sinan.hadisimvar.data.local.dao.** { *; }
-keep class com.sinan.hadisimvar.data.local.dao.** { *; }

# ─────────────────────────────────────────────────────────────────────
# ROOM DATABASE - KAPSAMLİ
# ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class *
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.paging.**

# Room generated classes
-keep class com.sinan.hadisimvar.data.local.AppDatabase_Impl { *; }
-keep class **_Impl { *; }

# ─────────────────────────────────────────────────────────────────────
# ANDROIDX & MATERIAL
# ─────────────────────────────────────────────────────────────────────
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Lifecycle
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel

# ─────────────────────────────────────────────────────────────────────
# OKHTTP & OKIO
# ─────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# ─────────────────────────────────────────────────────────────────────
# UYGULAMA ANA SINIFLARINI KORU
# ─────────────────────────────────────────────────────────────────────
-keep class com.sinan.hadisimvar.HadisApp { *; }
-keep class com.sinan.hadisimvar.widget.** { *; }
-keep class com.sinan.hadisimvar.ads.** { *; }
-keep class com.sinan.hadisimvar.ui.** { *; }

# Workers - Bildirimler için kritik
-keep class com.sinan.hadisimvar.workers.** { *; }
-keepclassmembers class com.sinan.hadisimvar.workers.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker

# Utils - ThemeHelper, NotificationHelper vb.
-keep class com.sinan.hadisimvar.utils.** { *; }
-keepclassmembers class com.sinan.hadisimvar.utils.** { *; }

# ─────────────────────────────────────────────────────────────────────
# GOOGLE ADMOB
# ─────────────────────────────────────────────────────────────────────
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
-keep class com.google.android.gms.** { *; }

# ─────────────────────────────────────────────────────────────────────
# ENUM KORUMA
# ─────────────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ─────────────────────────────────────────────────────────────────────
# SERIALIZABLE
# ─────────────────────────────────────────────────────────────────────
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ─────────────────────────────────────────────────────────────────────
# HATA AYIKLAMA İÇİN
# ─────────────────────────────────────────────────────────────────────
-renamesourcefileattribute SourceFile
