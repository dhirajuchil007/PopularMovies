package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Database.AppDatabase;
import com.example.android.popularmovies.Database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<MovieEntry>> movies;
    public MainViewModel(@NonNull Application application) {
                super(application);
        AppDatabase database=AppDatabase.getsInstance(this.getApplication());
        movies=database.moviesDao().loadAllMovies();
    }
    public  LiveData<List<MovieEntry>> getMovies(){
        return movies;
    }
}
