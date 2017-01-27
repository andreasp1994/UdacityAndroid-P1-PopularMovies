package com.apogiatzis.udacitymovieproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickListener{

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMoviesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mMoviesList;
    private Movie.MovieCategory activeMovieCategory;
    private int activeMoviePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mMoviesList = new ArrayList<>();
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_container);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRecyclerView.setLayoutManager(mGridLayoutManager);
        mMoviesRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mGridLayoutManager.findLastCompletelyVisibleItemPosition() == (mMoviesList.size() -1)){
                    boolean appendDataOnExistingList = true;
                    loadMovieData(activeMovieCategory, activeMoviePage+1, appendDataOnExistingList);
                }
            }
        });
        mMoviesRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        if(NetworkUtils.isOnline()){
            loadMovieData(Movie.MovieCategory.TOP_RATED, 1, false);
        } else {
            Toast.makeText(getApplicationContext(), "Check you internet connection!",Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMovieData(Movie.MovieCategory category, int page, boolean append){
        activeMovieCategory = category;
        activeMoviePage = page;
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(category, page, append);
        fetchMoviesTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.movies_menu_popular) {
            mMoviesAdapter.setMovieData(null);
            loadMovieData(Movie.MovieCategory.POPULAR, 1, false);
            return true;
        } else if(id == R.id.movies_menu_top_rated) {
            mMoviesAdapter.setMovieData(null);
            loadMovieData(Movie.MovieCategory.TOP_RATED, 1, false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MoviesActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Movie.INTENT_EXTRA_MOVIE_OBJECT, movie);
        startActivity(intent);
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private Movie.MovieCategory category;
        int page;
        boolean append;

        public FetchMoviesTask(Movie.MovieCategory category, int page, boolean append){
            this.category = category;
            this.page = page;
            this.append = append;
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... aVoid) {
            ArrayList<Movie> movies = null;
            try{
                movies = NetworkUtils.fetchMoviesByPage(category, page);
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if(movies != null){
                if (append)
                    mMoviesList.addAll(movies);
                else
                    mMoviesList = movies;
                mMoviesAdapter.setMovieData(mMoviesList);
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.movie_fetch_error_msg), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
