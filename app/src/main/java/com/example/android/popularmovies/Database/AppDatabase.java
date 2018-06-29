package com.example.android.popularmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;
@Database( entities ={MovieEntry.class},version = 1,exportSchema = false)
public  abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favourites";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context)
    {
        if(sInstance==null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database instance");
                //Temporary
                sInstance= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DATABASE_NAME).build();
            }
        }
        return sInstance;
    }
    public abstract MoviesDao moviesDao();
}
