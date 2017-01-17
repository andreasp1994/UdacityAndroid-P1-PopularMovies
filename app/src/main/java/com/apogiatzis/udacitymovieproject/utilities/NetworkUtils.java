package com.apogiatzis.udacitymovieproject.utilities;

import android.net.Uri;
import android.util.Log;
import com.apogiatzis.udacitymovieproject.models.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by andre on 16/01/2017.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String MOVIES_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static String API_KEY = "<PUT YOUR API KEY HERE>";

    public static ArrayList<Movie> fetchMoviesByPage(Movie.MovieCategory category, int page) throws IOException, JSONException{
        URL url = buildMoviesUrl(category, page);
        String jsonMovieData = getResponseFromHttpUrl(url);

        //Convert json response to java objects
        JSONObject movieData = new JSONObject(jsonMovieData);
        JSONArray movies = movieData.getJSONArray("results");
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        for(int i = 0 ; i < movies.length();i++){
            Movie movie = new Movie(movies.getJSONObject(i));
            movieList.add(movie);
        }

        return movieList;
    }

    private static URL buildMoviesUrl(Movie.MovieCategory category, int page) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL);

        switch (category){
            case TOP_RATED:
                builtUri = builtUri.buildUpon()
                        .appendEncodedPath("top_rated")
                        .build();
                break;
            case POPULAR:
                builtUri = builtUri.buildUpon()
                        .appendEncodedPath("popular")
                        .build();
                break;
        }

        builtUri = builtUri.buildUpon().appendQueryParameter("api_key", API_KEY )
                                    .appendQueryParameter("page", String.valueOf(page))
                                    .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildMoviePosterUrl(String posterPath, String size){
        Uri builtUri = Uri.parse(MOVIES_POSTER_BASE_URL).buildUpon()
                        .appendEncodedPath(size)
                        .appendPath(posterPath.substring(1))
                        .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
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
