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
    private MovieObject[] mv;
    private Context context;
    private String BASE_URL="http://image.tmdb.org/t/p/w185/";
private LayoutInflater inflater;
    public MovieAdapter(MovieObject[] mv, Context c) {
        this.mv = mv;
        this.context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public MovieAdapter(){}

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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);



        }
        ImageView poster = convertView.findViewById(R.id.poster);
        Picasso.with(context).load(BASE_URL + mv[position].imgPath).placeholder(R.drawable.placeholder).error(R.drawable.placeholder_error).into(poster);
        Log.d("PIC", BASE_URL + mv[position].title.trim());

        return convertView;
    }
}

