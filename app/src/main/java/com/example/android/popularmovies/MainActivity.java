package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    private GridView gd;
    private MovieAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    setupSharedPreferences();
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
        Log.d("check", "getData: "+sort);
        new GetMovies().execute(NetworkUtils.getUrl(apikey,sort));



    }
    //Used to get user preference (sortby opularity of rating)
    private String getSortFromSharedPreference(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(getString(R.string.sort_type_key),getString(R.string.sort_default));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_type_key)))
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
             gd= findViewById(R.id.grid_view_movies);
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
            }
            else {
                Toast toast=Toast.makeText(MainActivity.this,getString(R.string.no_internet_error_msg),Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings)
        {
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
