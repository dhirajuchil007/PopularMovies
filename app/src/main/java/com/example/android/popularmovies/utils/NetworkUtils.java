package com.example.android.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String BASE_QUERY="https://api.themoviedb.org/3/discover/movie";
    private final static String SORT="sort_by";
  //  final static  String sortby="popularity.desc";
    private final static String API_KEY="api_key";

    public static URL getUrl(String apikey,String sort) {
        // COMPLETED (1) Fill in this method to build the proper Github query URL
        Uri builtUri;
        if(sort.equals("popularity.desc")) {
             builtUri = Uri.parse(BASE_QUERY).buildUpon()

                    .appendQueryParameter(SORT, sort)
                    .appendQueryParameter(API_KEY, apikey)
                    .build();
        }
        else
        {

            builtUri = Uri.parse(BASE_QUERY.replace("/discover","")+"/top_rated").buildUpon()


                    .appendQueryParameter(API_KEY, apikey)
                    .build();
        }
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("URL", "getUrl: "+url);
        return url;
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
