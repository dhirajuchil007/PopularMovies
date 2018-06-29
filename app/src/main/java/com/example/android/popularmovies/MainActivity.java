package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.Database.AppDatabase;
import com.example.android.popularmovies.Database.MovieEntry;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    private GridView gd;
    private MovieAdapter adapter;
    private AppDatabase mDb;
    private Button tryAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    setupSharedPreferences();

        mDb=AppDatabase.getsInstance(getApplicationContext());
        tryAgain=findViewById(R.id.try_again_button);
        gd= findViewById(R.id.grid_view_movies);
        adapter=new MovieAdapter();
        getData();
       // String apikey=getString(R.string.api_key);
       // String ur= NetworkUtils.getUrl(apikey).toString();
       //] Log.d("url",ur);
      //  base.setText(ur);


    }

    private void setupSharedPreferences(){

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);


    }
    //Initates asynctask to get data from TMDB
    private void getData()
    {
        String apikey=getString(R.string.api_key);


        String sort=getSortFromSharedPreference();
        if(sort.equals(getString(R.string.fav_value)))
        {
            getFavouritesFromDb();
        }
        else {
            Log.d("check", "getData: " + sort);
            new GetMovies().execute(NetworkUtils.getUrl(apikey, sort));
        }



    }

    private void getFavouritesFromDb() {



        if(mDb.moviesDao().loadAllMovies()==null) {
            gd.setAdapter(null);
            return ;
        }
//       LiveData<List<MovieEntry>> movies= mDb.moviesDao().loadAllMovies();
        MainViewModel viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {

                ArrayList<MovieObject> mvForList=new ArrayList<>();
                Iterator itr=movieEntries.iterator();

                while(itr.hasNext())
                {
                    MovieEntry mvs=(MovieEntry) itr.next();
                    mvForList.add(new MovieObject(mvs.getId(),mvs.getTitle(),mvs.getPath(),mvs.getOverView(),mvs.getVoteAverage(),mvs.getReleaseDate()));


                }
                final MovieObject[] m=mvForList.toArray(new MovieObject[mvForList.size()]);
                if(m.length==0) {
                    gd.setAdapter(null);
                    createNoMoviesToast();

                    return;
                }
                String sort=getSortFromSharedPreference();
                Log.d(sort, "onChanged: "+sort);
                if(sort.equals(getString(R.string.fav_value))) {
                    adapter = new MovieAdapter(m, MainActivity.this);
                    gd.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    gd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this, DetailsAcitvity.class);
                            intent.putExtra("detailsObject", m[position]);
                            startActivity(intent);


                        }
                    });
                }

            }
        });



                                  }

    private void createNoMoviesToast() {
        Toast toast =Toast.makeText(this,getString(R.string.fav_list_empty),Toast.LENGTH_SHORT);
        toast.show();
    }


    //Used to get user preference (sortby opularity of rating)
    private String getSortFromSharedPreference(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(getString(R.string.sort_type_key),getString(R.string.sort_default));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_type_key))) {
            gd.setAdapter(null);
            getData();
            adapter.notifyDataSetChanged();
        }
        if(key.equals(R.string.fav_list))
        {
            getData();
            adapter.notifyDataSetChanged();
        }


    }
    //checks if device is connectd to Internet
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
    public  void retry(View view){
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public  class GetMovies extends AsyncTask<URL,Void,MovieObject[]>{

        @Override
        protected MovieObject[] doInBackground(URL... urls) {
            URL searchURL=urls[0];
            MovieObject[] androidAdapter;
            String mResults=null;
            if(isOnline()) {
                try {
                    mResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

                return null;
            }
            if(mResults!=null)
            {
                try {
                    JSONObject jsonObject=new JSONObject(mResults);
                    JSONArray results=jsonObject.getJSONArray("results");
                    androidAdapter=new MovieObject[results.length()];
                    Log.d("Results",Integer.toString(results.length()));
                    for(int i=0;i<results.length();i++)
                    {
                        JSONObject m=results.getJSONObject(i);

                        int id=m.getInt("id");
                        Log.d("idisthis",Integer.toString(id));

                        String title=m.getString("original_title");
                        String path=m.getString("poster_path").trim();

                        String overview=m.getString("overview");
                        double voteaverage=Double.parseDouble(m.getString("vote_average"));
                        String releaseDate=m.getString("release_date");
                        androidAdapter[i]=new MovieObject(id,title,path,overview,voteaverage,releaseDate);

                    }
                    return androidAdapter;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        protected void onPostExecute(final MovieObject results[]) {
            super.onPostExecute(results);
        //    Log.d("path",results[19].title);

          //  Log.d("array", "onPostExecute:"+results[0].imgPath);
            //Log.d("array", "onPostExecute:"+results[1].imgPath);
            if(results!=null)
            {
                adapter=new MovieAdapter(results,MainActivity.this);
            gd.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            gd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(MainActivity.this,DetailsAcitvity.class);
                    intent.putExtra("detailsObject",results[position]);
                    startActivity(intent);

                }
            });
            tryAgain.setVisibility(View.INVISIBLE
            );
            }
            else {
                Toast toast=Toast.makeText(MainActivity.this,getString(R.string.no_internet_error_msg),Toast.LENGTH_SHORT);

                toast.show();
                tryAgain.setVisibility(View.VISIBLE);
            }

        }
    }





}
