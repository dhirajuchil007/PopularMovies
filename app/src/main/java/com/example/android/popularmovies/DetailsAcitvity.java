package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsAcitvity extends AppCompatActivity {
    private String BASE_URL="http://image.tmdb.org/t/p/w185/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_acitvity);
        ImageView poster= findViewById(R.id.movie_poster);
        TextView title= findViewById(R.id.movie_title_tv);
        TextView description= findViewById(R.id.description_tv);
        TextView ratings= findViewById(R.id.rating_tv);
        TextView releaseDate= findViewById(R.id.release_date);
        Intent i=getIntent();
        MovieObject mv= (MovieObject) i.getSerializableExtra("detailsObject");
        Picasso.with(this).load(BASE_URL+mv.imgPath).into(poster);
        title.setText(mv.title);
        description.setText(mv.overview);
        ratings.setText(Double.toString(mv.voteAverage)+getString(R.string.divide10));
        //Formatting date from yyyy-MM-dd to dd-MM-yyyy
        SimpleDateFormat releaseDateFromatter=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat actudalDate=new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d=releaseDateFromatter.parse(mv.releaseDate);
            releaseDate.setText("Release Date: "+actudalDate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
