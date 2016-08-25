package com.marnun.popularmoviesapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String ARG_MOVIE = "movie";
    @BindView(R.id.movie_detail)
    TextView mMoviePlotView;
    @BindView(R.id.original_title)
    TextView mTitleView;
    @BindView(R.id.rating)
    TextView mRatingView;
    @BindView(R.id.release_date)
    TextView mReleaseDateView;
    @BindView(R.id.movie_detail_poster)
    ImageView mPosterDetailView;
    private Movie mMovie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = (Movie) getArguments().getSerializable(ARG_MOVIE);

            setImageToolbar();
        }
    }

    private void setImageToolbar() {
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle("");
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/original" + mMovie.getBackdropPath())
                    .into((ImageView) appBarLayout.findViewById(R.id.image_collapsing_toolbar));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setImageToolbar();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (mMovie != null) {

            mMoviePlotView.setText(mMovie.getPlotSynopsis());

            mTitleView.setText(mMovie.getOriginalTitle());

            String ratingString = String.format(getString(R.string.rating), mMovie.getUserRating().toString());
            mRatingView.setText(ratingString);

            String releaseDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mMovie.getReleaseDate());
            String dateString = String.format(getString(R.string.release_date), releaseDate);
            mReleaseDateView.setText(dateString);

            int posterWidth = MovieListActivity.getPosterWidth();
            int posterHeight = MovieListActivity.getPosterHeight();
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w500" + mMovie.getPosterPath())
                    .resize(posterWidth, posterHeight)
                    .into(mPosterDetailView);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

            Retrofit retrofitTrailers = new Retrofit.Builder()
                    .baseUrl(TrailersService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            TrailersService trailersService = retrofitTrailers.create(TrailersService.class);
            Call<Trailers> callTrailers = trailersService.loadTrailers(mMovie.getId(), BuildConfig.API_KEY);
            callTrailers.enqueue(new Callback<Trailers>() {
                @Override
                public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                    Trailers trailers = response.body();
                    for (Trailer trailer : trailers.results) {
                        Log.d(LOG_TAG, trailer.toString());
                    }
                }
                @Override
                public void onFailure(Call<Trailers> call, Throwable t) {

                }
            });

            Retrofit retrofitReviews = new Retrofit.Builder()
                    .baseUrl(ReviewsService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            ReviewsService reviewsService = retrofitReviews.create(ReviewsService.class);
            Call<Reviews> callReviews = reviewsService.loadReviews(mMovie.getId(), BuildConfig.API_KEY);
            callReviews.enqueue(new Callback<Reviews>() {
                @Override
                public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                    Reviews reviews = response.body();
                    for (Review review : reviews.results) {
                        Log.d(LOG_TAG, review.toString());
                    }
                }

                @Override
                public void onFailure(Call<Reviews> call, Throwable t) {

                }
            });
        }

        return rootView;
    }

}
