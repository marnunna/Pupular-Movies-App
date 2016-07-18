package com.marnun.popularmoviesapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String ARG_MOVIE = "movie";

    private Movie mMovie;

    @BindView(R.id.movie_detail) TextView mMoviePlotView;
    @BindView(R.id.original_title) TextView mTitleView;
    @BindView(R.id.rating) TextView mRatingView;
    @BindView(R.id.release_date) TextView mReleaseDateView;
    @BindView(R.id.movie_detail_poster) ImageView mPosterDetailView;

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
                    .load("http://image.tmdb.org/t/p/w500"+mMovie.getPosterPath())
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
                    .load("http://image.tmdb.org/t/p/w500"+mMovie.getPosterPath())
                    .resize(posterWidth, posterHeight)
                    .into(mPosterDetailView);
        }

        return rootView;
    }
}
