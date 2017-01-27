package com.apogiatzis.udacitymovieproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView mMovieTitleTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieOverviewTextView;
    private ImageView mMoviePosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie movie = getIntent().getExtras().getParcelable(Movie.INTENT_EXTRA_MOVIE_OBJECT);

        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_details_poster);
        mMovieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_details_release_date);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_details_rating);
        mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_details_overview);

        mMovieTitleTextView.setText(movie.getTitle());
        mMovieReleaseDateTextView.setText(movie.getReleaseDate());
        mMovieRatingTextView.setText(String.valueOf(movie.getVoteAverage()));
        mMovieOverviewTextView.setText(movie.getOverview());

        String posterUrl = NetworkUtils.buildMoviePosterUrl(movie.getPosterPath(), Movie.MOVIE_POSTER_DEFAULT_SIZE).toString();
        Picasso.with(mMovieTitleTextView.getContext()).load(posterUrl).into(mMoviePosterImageView);
    }
}
