package com.sinan.hadisimvar.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sinan.hadisimvar.data.local.dao.HadithDao;
import com.sinan.hadisimvar.data.local.entity.Hadith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Hadith.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract HadithDao hadithDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "hadith_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // Bu noktada Context'e erişmemiz lazım.
                // Context'i getDatabase metodunda alıp static bir değişkene atayabiliriz veya
                // Worker kullanabiliriz. Basitlik için burada Application Context'i
                // kullanacağız
                // ancak callback içinde context'e doğrudan erişim yoktur.
                // Bu yüzden populate işlemini SplashActivity veya Repository içinde ilk
                // açılışta kontrol edip yapmak daha güvenlidir.
                // Ancak Room callback ile de yapılabilir eğer context'i statik tutarsak (memory
                // leak riski olsa da basit projede kabul edilebilir).
                // Düzeltme: En temiz yöntem, Repository başlatıldığında veritabanı boşsa
                // doldurmaktır.
                // Bu callback şimdilik boş bırakılıyor, işlem Repository'de yapılacak.
            });
        }
    };
}
