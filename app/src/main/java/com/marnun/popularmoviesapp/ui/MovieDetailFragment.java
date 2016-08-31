package com.marnun.popularmoviesapp.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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

                if (Utility.isConnected(activity)) {

                    //load backdrop image from web
                    Picasso.with(activity)
                            .load("http://image.tmdb.org/t/p/original" + backdropPath)
                            .into((ImageView) appBarLayout.findViewById(R.id.image_collapsing_toolbar));
                } else {

                    //load backdrop image from memory
                    ContextWrapper cw = new ContextWrapper(activity);
                    File backdropDirectory = cw.getDir(Utility.BACKDROP_DIRECTORY, Context.MODE_PRIVATE);
                    File backdropFile = new File(backdropDirectory, mMovie.getId()+".jpeg");
                    Picasso.with(activity)
                            .load(backdropFile)
                            .into((ImageView) appBarLayout.findViewById(R.id.image_collapsing_toolbar));
                }

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

            mTitleView.setText(mMovie.getOriginalTitle());

            mMoviePlotView.setText(mMovie.getPlotSynopsis());

            String ratingString = String.format(getString(R.string.rating), mMovie.getUserRating().toString());
            mRatingView.setText(ratingString);

            String releaseDate = Utility.sdf.format(mMovie.getReleaseDate());
            String dateString = String.format(getString(R.string.release_date), releaseDate);
            mReleaseDateView.setText(dateString);

            String posterPath = mMovie.getPosterPath();
            int posterWidth = MovieListActivity.getPosterWidth();
            int posterHeight = MovieListActivity.getPosterHeight();

            if (Utility.isConnected(getActivity())) {

                //load poster image from web
                if (null != posterPath) {

                    Picasso.with(getActivity())
                            .load("http://image.tmdb.org/t/p/w780" + posterPath)
                            .resize(posterWidth, posterHeight)
                            .into(mPosterDetailView);

                }

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

                //load trailers from web using retrofit
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
                        TrailersAdapter trailersAdapter = new TrailersAdapter(getActivity(), trailers);
                        mTrailerList.setAdapter(trailersAdapter);
                    }

                    @Override
                    public void onFailure(Call<Trailers> call, Throwable t) {

                    }
                });

                //load reviews from web using retrofit
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
                        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
                        mReviewsList.setAdapter(reviewsAdapter);
                    }

                    @Override
                    public void onFailure(Call<Reviews> call, Throwable t) {

                    }
                });

            } else {

                //load poster image from memory
                ContextWrapper cw = new ContextWrapper(getActivity());
                File posterDirectory = cw.getDir(Utility.POSTER_DIRECTORY, Context.MODE_PRIVATE);
                File posterFile = new File(posterDirectory, mMovie.getId()+".jpeg");
                Picasso.with(getActivity())
                        .load(posterFile)
                        .resize(posterWidth, posterHeight)
                        .into(mPosterDetailView);

            }

            mFavoriteButton.setVisibility(Utility.isFavorite(getActivity(), mMovie) ? View.GONE : View.VISIBLE);

            mTrailersLabel.setVisibility(Utility.isConnected(getActivity()) ? View.VISIBLE : View.GONE);
            mReviewsLabel.setVisibility(Utility.isConnected(getActivity()) ? View.VISIBLE : View.GONE);
            mReviewsList.setVisibility(Utility.isConnected(getActivity()) ? View.VISIBLE : View.GONE);
            mTrailerList.setVisibility(Utility.isConnected(getActivity()) ? View.VISIBLE : View.GONE);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), R.string.favorite_button_message, Toast.LENGTH_LONG).show();

                    //inserting movie in database
                    ContentValues values = new ContentValues();
                    values.put(MovieColumns.ID, mMovie.getId());
                    values.put(MovieColumns.ORIGINAL_TITLE, mMovie.getOriginalTitle());
                    values.put(MovieColumns.OVERVIEW, mMovie.getPlotSynopsis());
                    values.put(MovieColumns.VOTE_AVERAGE, mMovie.getUserRating());
                    values.put(MovieColumns.RELEASE_DATE, Utility.sdf.format(mMovie.getReleaseDate()));
                    values.put(MovieColumns.POSTER_PATH, mMovie.getPosterPath());
                    values.put(MovieColumns.BACKDROP_PATH, mMovie.getBackdropPath());

                    //save poster image using picasso
                    Picasso.with(getActivity())
                            .load("http://image.tmdb.org/t/p/w780" + mMovie.getPosterPath())
                            .into(picassoImageTarget(getActivity(), Utility.POSTER_DIRECTORY, mMovie.getId()+".jpeg"));

                    //save backdrop image using picasso
                    Picasso.with(getActivity())
                            .load("http://image.tmdb.org/t/p/original" + mMovie.getBackdropPath())
                            .into(picassoImageTarget(getActivity(), Utility.BACKDROP_DIRECTORY, mMovie.getId()+".jpeg"));

                    getContext().getContentResolver().insert(MovieProvider.Movies.CONTENT_URI, values);

                    mFavoriteButton.setVisibility(View.GONE);
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

    /*
    * This method return a target required by picasso for downloading images in memory
     */
    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

}
