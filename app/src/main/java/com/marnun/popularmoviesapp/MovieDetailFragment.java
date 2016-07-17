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

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE = "movie";

    /**
     * The dummy content this fragment is presenting.
     */
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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
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

        // Show the dummy content as text in a TextView.
        if (mMovie != null) {

            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(mMovie.getPlotSynopsis());

            ((TextView) rootView.findViewById(R.id.original_title)).setText(mMovie.getOriginalTitle());

            String ratingString = String.format(getString(R.string.rating), mMovie.getUserRating().toString());
            ((TextView) rootView.findViewById(R.id.rating)).setText(ratingString);

            String releaseDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mMovie.getReleaseDate());
            String dateString = String.format(getString(R.string.release_date), releaseDate);
            ((TextView) rootView.findViewById(R.id.release_date)).setText(dateString);

            int posterWidth = MovieListActivity.getPosterWidth();
            int posterHeight = MovieListActivity.getPosterHeight();
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w500"+mMovie.getPosterPath())
                    .resize(posterWidth, posterHeight)
                    .into((ImageView) rootView.findViewById(R.id.movie_detail_poster));
        }

        return rootView;
    }
}
