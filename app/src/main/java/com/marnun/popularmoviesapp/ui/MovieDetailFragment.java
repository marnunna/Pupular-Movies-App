package com.marnun.popularmoviesapp.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marnun.popularmoviesapp.BuildConfig;
import com.marnun.popularmoviesapp.R;
import com.marnun.popularmoviesapp.data.db.MovieColumns;
import com.marnun.popularmoviesapp.data.db.MovieProvider;
import com.marnun.popularmoviesapp.data.model.Movie;
import com.marnun.popularmoviesapp.data.model.Review;
import com.marnun.popularmoviesapp.data.model.Trailer;
import com.marnun.popularmoviesapp.data.network.Reviews;
import com.marnun.popularmoviesapp.data.network.ReviewsService;
import com.marnun.popularmoviesapp.data.network.Trailers;
import com.marnun.popularmoviesapp.data.network.TrailersService;
import com.marnun.popularmoviesapp.utility.Utility;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
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

    @BindView(R.id.movie_synopsis)
    TextView mMoviePlotView;

    @BindView(R.id.original_title)
    TextView mTitleView;

    @BindView(R.id.rating)
    TextView mRatingView;

    @BindView(R.id.release_date)
    TextView mReleaseDateView;

    @BindView(R.id.movie_detail_poster)
    ImageView mPosterDetailView;

    @BindView(R.id.reviews_list)
    RecyclerView mReviewsList;

    @BindView(R.id.trailers_list)
    RecyclerView mTrailerList;

    @BindView(R.id.favorite_button)
    Button mFavoriteButton;

    @BindView(R.id.reviews_label)
    TextView mReviewsLabel;

    @BindView(R.id.trailers_label)
    TextView mTrailersLabel;

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
        String backdropPath = mMovie.getBackdropPath();
        if (null != backdropPath) {
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("");

                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/original" + backdropPath)
                        .into((ImageView) appBarLayout.findViewById(R.id.image_collapsing_toolbar));
            }
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

            String releaseDate = Utility.sdf.format(mMovie.getReleaseDate());
            String dateString = String.format(getString(R.string.release_date), releaseDate);
            mReleaseDateView.setText(dateString);

            String posterPath = mMovie.getPosterPath();
            if (null != posterPath) {
                int posterWidth = MovieListActivity.getPosterWidth();
                int posterHeight = MovieListActivity.getPosterHeight();
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w500" + posterPath)
                        .resize(posterWidth, posterHeight)
                        .into(mPosterDetailView);

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

                Retrofit retrofitTrailers = new Retrofit.Builder()
                        .baseUrl(TrailersService.ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                TrailersService trailersService = retrofitTrailers.create(TrailersService.class);
                Call<Trailers> callTrailers = trailersService.loadTrailers(mMovie.getId(), BuildConfig.MOVIE_DATABASE_API_KEY);
                callTrailers.enqueue(new Callback<Trailers>() {
                    @Override
                    public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                        List<Trailer> trailers = response.body().results;
                        Log.d(LOG_TAG, trailers.toString());
//                    mTrailerList.setHasFixedSize(true);
                        TrailersAdapter trailersAdapter = new TrailersAdapter(getActivity(), trailers);
                        mTrailerList.setAdapter(trailersAdapter);
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
                Call<Reviews> callReviews = reviewsService.loadReviews(mMovie.getId(), BuildConfig.MOVIE_DATABASE_API_KEY);
                callReviews.enqueue(new Callback<Reviews>() {
                    @Override
                    public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                        List<Review> reviews = response.body().results;
                        Log.d(LOG_TAG, reviews.toString());
                        mReviewsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
                        mReviewsList.setAdapter(reviewsAdapter);
                    }

                    @Override
                    public void onFailure(Call<Reviews> call, Throwable t) {

                    }
                });

                mFavoriteButton.setVisibility(View.VISIBLE);
                mTrailersLabel.setVisibility(View.VISIBLE);
                mReviewsLabel.setVisibility(View.VISIBLE);
                mReviewsList.setVisibility(View.VISIBLE);
                mTrailerList.setVisibility(View.VISIBLE);
                mPosterDetailView.setVisibility(View.VISIBLE);

            } else {

                mFavoriteButton.setVisibility(View.GONE);
                mTrailersLabel.setVisibility(View.GONE);
                mReviewsLabel.setVisibility(View.GONE);
                mReviewsList.setVisibility(View.GONE);
                mTrailerList.setVisibility(View.GONE);
                mPosterDetailView.setVisibility(View.GONE);

            }

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), R.string.favorite_button_message, Toast.LENGTH_LONG).show();
                    ContentValues values = new ContentValues();
                    values.put(MovieColumns.ID, mMovie.getId());
                    values.put(MovieColumns.ORIGINAL_TITLE, mMovie.getOriginalTitle());
                    values.put(MovieColumns.OVERVIEW, mMovie.getPlotSynopsis());
                    values.put(MovieColumns.VOTE_AVERAGE, mMovie.getUserRating());
                    values.put(MovieColumns.RELEASE_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mMovie.getReleaseDate()));
                    getContext().getContentResolver().insert(MovieProvider.Movies.CONTENT_URI, values);
                }
            });

        }

        return rootView;
    }

    public class ReviewsAdapter
            extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

        private final List<Review> mValues;

        public ReviewsAdapter(List<Review> items) {
            mValues = items;
        }

        @Override
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_item, parent, false);
            return new ReviewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReviewHolder holder, int position) {
            Review review = mValues.get(position);
            holder.authorView.setText(review.getAuthor());
            holder.contentView.setText(review.getContent());
        }

        @Override
        public int getItemCount() {
            return (null != mValues ? mValues.size() : 0);
        }

        public class ReviewHolder extends RecyclerView.ViewHolder {
            public TextView authorView;
            public TextView contentView;

            public ReviewHolder(View view) {
                super(view);
                authorView = (TextView) view.findViewById(R.id.review_author);
                contentView = (TextView) view.findViewById(R.id.review_content);
            }

        }
    }

}
