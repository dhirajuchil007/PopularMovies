package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface MoviesDao {
    @Query("Select * from movies Order By voteAverage")
    LiveData<List<MovieEntry>> loadAllMovies();

    @Insert
    void insert(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);
}
