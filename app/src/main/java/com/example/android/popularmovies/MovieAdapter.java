package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends BaseAdapter {
    MovieObject mv[];
    Context context;
    String BASE_URL="http://image.tmdb.org/t/p/w185/";
LayoutInflater inflater;
    public MovieAdapter(MovieObject[] mv, Context c) {
        this.mv = mv;
        this.context = c;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mv.length;
    }

    @Override
    public MovieObject getItem(int position) {
        return mv[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);



        }
        ImageView poster = convertView.findViewById(R.id.poster);
        Picasso.with(context).load(BASE_URL + mv[position].imgPath).into(poster);
        Log.d("PIC", BASE_URL + mv[position].title.trim());

        return convertView;
    }
}

