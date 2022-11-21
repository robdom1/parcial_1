package com.example.parcial_1.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.parcial_1.entities.Producto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Producto.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static String NAME = "pucmm";
    private static volatile AppDatabase INSTANCE;

    public abstract ProductoDao productoDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{

        private ProductoDao productoDao;

        private PopulateDBAsyncTask(AppDatabase db) {
            this.productoDao = db.productoDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            productoDao.insert(new Producto("prod", "test", 100.00));
            productoDao.insert(new Producto("prod2", "test2", 200.00));
            productoDao.insert(new Producto("prod3", "test3", 300.00));
            productoDao.insert(new Producto("prod4", "test4", 400.00));
            productoDao.insert(new Producto("prod5", "test5", 500.00));
            return null;
        }
    }
}
