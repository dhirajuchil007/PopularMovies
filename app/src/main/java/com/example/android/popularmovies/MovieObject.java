package com.example.android.popularmovies;

import java.io.Serializable;

public class MovieObject implements Serializable {
    String title;
    String imgPath;
    String overview;
    double voteAverage;
    String releaseDate;

    public MovieObject(String title, String imgPath, String overview, double voteAverage, String releaseDate) {
        this.title = title;
        this.imgPath = imgPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
}
