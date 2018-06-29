package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Database.AppDatabase;
import com.example.android.popularmovies.Database.MovieEntry;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DetailsAcitvity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {
    private String BASE_URL="http://image.tmdb.org/t/p/w185/";
    private String TRAILER_BASE_URL="https://api.themoviedb.org/3/movie/";
    private TextView reviewContent;
    private TextView offlineMode;
    private RecyclerView trailerRecyclerView;
    private TextView offlineForReviews;
    private TrailerAdapter trailerAdapter;
    Set<String> set;
    private AppDatabase mDb;
    Set<String> defaultList;
    MovieObject mv;
    private  ImageView favouritesStar;
    Context context;
    SharedPreferences sharedpref;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultList=new HashSet<>();
        defaultList.add("0");
        super.onCreate(savedInstanceState);
        context=getApplicationContext();

        mDb=AppDatabase.getsInstance(getApplicationContext());
        ActionBar actionBar=this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // id=0;
        setContentView(R.layout.activity_details_acitvity);
        offlineMode=findViewById(R.id.offline_text_view);
        offlineForReviews=findViewById(R.id.offline_text_view_reviews);
        ImageView poster= findViewById(R.id.movie_poster);
        TextView title= findViewById(R.id.movie_title_tv);
        TextView description= findViewById(R.id.description_tv);
        TextView ratings= findViewById(R.id.rating_tv);
        TextView releaseDate= findViewById(R.id.release_date);
        favouritesStar=findViewById(R.id.favourites_icon);
        Intent i=getIntent();
         mv= (MovieObject) i.getSerializableExtra("detailsObject");
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


        reviewContent=findViewById(R.id.review_content);
        try {
            String url=TRAILER_BASE_URL+id+"/reviews";
            Uri temp=Uri.parse(url).buildUpon().appendQueryParameter("api_key",getString(R.string.api_key)).build();
            URL reviews=new URL(temp.toString());
            new GetReviews().execute(reviews);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        sharedpref=context.getSharedPreferences(getString(R.string.fav_list),Context.MODE_PRIVATE);
        int defaultValue=0;
         set =sharedpref.getStringSet(getString(R.string.fav_list),defaultList);
        Log.d("set", "onCreate: "+set.toString());
        if(set.contains(Integer.toString(id)))
            favouritesStar.setImageResource(R.drawable.favourites_star);
        else
            favouritesStar.setImageResource(R.drawable.favourites_star_empty);



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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
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
    public void setFavourites(View view){
         set =sharedpref.getStringSet(getString(R.string.fav_list),defaultList);
        SharedPreferences.Editor editor=sharedpref.edit();
        editor.clear();

        if(set.contains(Integer.toString(id)))
        {
            deleteMovie();
            favouritesStar.setImageResource(R.drawable.favourites_star_empty);
            set.remove(Integer.toString(id));
            editor.putStringSet(getString(R.string.fav_list),set);

            editor.commit();
        }
        else {
                addMovie();
            favouritesStar.setImageResource(R.drawable.favourites_star);
            set.add(Integer.toString(id));
            editor.putStringSet(getString(R.string.fav_list),set);
            editor.commit();

        }


    }

    private void deleteMovie() {
        int movId=id;
        String title=mv.title;
        String imgpath=mv.imgPath;
        String overview=mv.overview;
        double voteavg=mv.voteAverage;
        String releaseDate=mv.releaseDate;
        final MovieEntry movieEntry=new MovieEntry(movId,title,imgpath,overview,voteavg,releaseDate);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.moviesDao().deleteMovie(movieEntry);
            }
        });

        Toast toast=Toast.makeText(context,getString(R.string.movie_deleted),Toast.LENGTH_SHORT);
        toast.show();

    }

    private void addMovie() {
        int movId=id;
        String title=mv.title;
        String imgpath=mv.imgPath;
        String overview=mv.overview;
        double voteavg=mv.voteAverage;
        String releaseDate=mv.releaseDate;
        final MovieEntry movieEntry=new MovieEntry(movId,title,imgpath,overview,voteavg,releaseDate);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.moviesDao().insert(movieEntry);
            }
        });

        Toast toast=Toast.makeText(context,getString(R.string.movie_added),Toast.LENGTH_SHORT);
        toast.show();
    }

    public  class  GetTrailers extends AsyncTask<URL,Void,List<TrailerObject>>{

        @Override
        protected List<TrailerObject> doInBackground(URL... urls) {
            URL searchUrl=urls[0];

            List<TrailerObject> trailerList= new ArrayList<>();
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
                offlineMode.setVisibility(View.VISIBLE);
            }

        }
    }
    public class GetReviews extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL url=urls[0];
            String results=null;
            String review= "";
            if(isOnline())
            {
                try {
                    results= NetworkUtils.getResponseFromHttpUrl(url);
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
                    Log.d("result", "doInBackground: "+resultsJson.length());

                        if(resultsJson.length()>0) {
                            JSONObject m = resultsJson.getJSONObject(0);
                            review = m.getString("content");
                        }
                        else
                            review=getString(R.string.review_not_found);





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return review;
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
        protected void onPostExecute(String s) {
            if(s!=null)
            {
                reviewContent.setText(s);
            }
            else
            {
                offlineForReviews.setVisibility(View.VISIBLE);
            }
        }
    }
}
