package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsAcitvity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {
    private String BASE_URL="http://image.tmdb.org/t/p/w185/";
    private String TRAILER_BASE_URL="https://api.themoviedb.org/3/movie/";

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id=0;
        setContentView(R.layout.activity_details_acitvity);
        ImageView poster= findViewById(R.id.movie_poster);
        TextView title= findViewById(R.id.movie_title_tv);
        TextView description= findViewById(R.id.description_tv);
        TextView ratings= findViewById(R.id.rating_tv);
        TextView releaseDate= findViewById(R.id.release_date);
        Intent i=getIntent();
        MovieObject mv= (MovieObject) i.getSerializableExtra("detailsObject");
        id=mv.id;
        Log.d("IDIS", "onCreate: "+mv.id);
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
        trailerRecyclerView=findViewById(R.id.trailer_list);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        trailerRecyclerView.setLayoutManager(layoutManager);
        try {
            String url=TRAILER_BASE_URL+id+"/videos";
            Uri temp=Uri.parse(url).buildUpon().appendQueryParameter("api_key",getString(R.string.api_key)).build();
            URL trailers=new URL(temp.toString());
            new GetTrailers().execute(trailers);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }








    }
    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    @Override
    public void onClick(String url) {
      //  Uri uri=NetworkUtils.getYoutubeURL(url);
        Intent youtubeIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:"+url));
        Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v="+url));
        try {
            startActivity(youtubeIntent);
        }
        catch (ActivityNotFoundException ex){
            startActivity(browserIntent);
        }

        startActivity(youtubeIntent);

    }

    public  class  GetTrailers extends AsyncTask<URL,Void,List<TrailerObject>>{

        @Override
        protected List<TrailerObject> doInBackground(URL... urls) {
            URL searchUrl=urls[0];

            List<TrailerObject> trailerList=new ArrayList<TrailerObject>();
            String results=null;
            if(isOnline())
            {
                try {
                    results= NetworkUtils.getResponseFromHttpUrl(searchUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else return null;
            if(results!=null)
            {

                try {
                    JSONObject jsonObject=new JSONObject(results);
                    JSONArray resultsJson=jsonObject.getJSONArray("results");
                    for(int i=0;i<results.length();i++) {
                        JSONObject m = resultsJson.getJSONObject(i);
                        Log.d("trailerresponse", "doInBackground: "+m.toString());
                        TrailerObject t=new TrailerObject(m.getString("type"),m.getString("key"));
                        trailerList.add(t);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return trailerList;

            }
            else {
                Log.e("Json", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. ",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TrailerObject> trailerObjects) {
            if(trailerObjects!=null) {
                trailerAdapter = new TrailerAdapter(trailerObjects,DetailsAcitvity.this);
                trailerRecyclerView.setAdapter(trailerAdapter);

            }
            else
            {
                Toast toast=Toast.makeText(DetailsAcitvity.this,getString(R.string.no_internet_error_msg),Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }
}
