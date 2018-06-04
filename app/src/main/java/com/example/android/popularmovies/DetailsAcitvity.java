package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsAcitvity extends AppCompatActivity {
    String BASE_URL="http://image.tmdb.org/t/p/w185/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_acitvity);
        ImageView poster=(ImageView)findViewById(R.id.movie_poster);
        TextView title=(TextView) findViewById(R.id.movie_title_tv);
        TextView description=(TextView)findViewById(R.id.description_tv);
        TextView ratings=(TextView)findViewById(R.id.rating_tv);
        Intent i=getIntent();
        MovieObject mv= (MovieObject) i.getSerializableExtra("detailsObject");
        Picasso.with(this).load(BASE_URL+mv.imgPath).into(poster);
        title.setText(mv.title);
        description.setText(mv.overview);
        ratings.setText(Double.toString(mv.voteAverage)+"/10");



    }
}
