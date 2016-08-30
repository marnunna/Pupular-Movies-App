package com.marnun.popularmoviesapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.marnun.popularmoviesapp.data.network.Movies;
import com.marnun.popularmoviesapp.data.network.MoviesService;
import com.marnun.popularmoviesapp.settings.SettingsActivity;
import com.marnun.popularmoviesapp.utility.Utility;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements Callback<Movies>, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static RecyclerView mRecyclerView;
    private static int mColumnsNo;
    private static String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mSortCriteria = Utility.getSortCriteria(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.movie_list);
        assert mRecyclerView != null;
        loadMovies();

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortCriteria = Utility.getSortCriteria(this);
        if(!mSortCriteria.equals(sortCriteria)){
            loadMovies();
            mSortCriteria = sortCriteria;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MoviesRecyclerViewAdapter
            extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieHolder> {

        private final String LOG_TAG = MoviesRecyclerViewAdapter.class.getSimpleName();
        private final List<Movie> mValues;

        public MoviesRecyclerViewAdapter(List<Movie> items) {
            mValues = items;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(final MovieHolder holder, int position) {
            holder.mMovie =  mValues.get(position);
            String title = holder.mMovie.getOriginalTitle();
            holder.mTitleView.setText(title);
            holder.mView.setMinimumWidth(getPosterWidth());
            String posterPath = holder.mMovie.getPosterPath();
            if (null != posterPath) {
                holder.mView.setMinimumHeight(getPosterHeight());
                String uri = "http://image.tmdb.org/t/p/w780";
                uri += posterPath;
                Picasso.with(getBaseContext())
                        .load(uri)
                        .resize(getPosterWidth() - 50, getPosterHeight() - 50)
                        .into(holder.mPosterView);
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, holder.mMovie);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_MOVIE, holder.mMovie);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != mValues ? mValues.size() : 0);
        }

        public class MovieHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mPosterView;
            public final TextView mTitleView;
            public Movie mMovie;

            public MovieHolder(View view) {
                super(view);
                mView = view;
                mPosterView = (ImageView) view.findViewById(R.id.poster_view);
                mTitleView = (TextView) view.findViewById(R.id.title_item_view);
            }

        }
    }

    public static int getPosterWidth() {
        //The width of every poster is calculated dividing the width of the grid by the number of columns
        return mRecyclerView.getWidth() / mColumnsNo;
    }

    public static int getPosterHeight() {
        return (int) (getPosterWidth() * 1.5);
    }


    private void loadMovies() {

//        MoviesTask moviesTask = new MoviesTask();
//        moviesTask.execute();

        String sortCriteria = Utility.getSortCriteria(this);

        if (sortCriteria.equals(getString(R.string.order_favorites_value))) {

            //get movies from loader
            getSupportLoaderManager().initLoader(FAVORITES_LOADER, null, this);

        } else {

            //get movies from api

            //check for internet connection
            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {

                String apiKey = BuildConfig.MOVIE_DATABASE_API_KEY;

                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd")
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MoviesService.ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                // prepare call in Retrofit 2.0
                MoviesService moviesService = retrofit.create(MoviesService.class);

                Call<Movies> call = moviesService.loadMovies(sortCriteria, apiKey);
                //asynchronous call
                call.enqueue(this);
            } else {

                TextView errorView = (TextView) findViewById(R.id.error_textview);
                errorView.setText(R.string.error_connection);
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayMovies(List<Movie> movies) {
        TextView errorTextView = (TextView) findViewById(R.id.error_textview);
        if (movies != null) {

            errorTextView.setVisibility(View.GONE);

            //if is in portrait mode the grid of films have 2 columns, if in landscape 3
            mColumnsNo = 2;
            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                mColumnsNo = 3;
            }

            GridLayoutManager layoutManager = new GridLayoutManager(MovieListActivity.this, mColumnsNo);
            mRecyclerView.setLayoutManager(layoutManager);

            MoviesRecyclerViewAdapter moviesAdapter = new MoviesRecyclerViewAdapter(movies);
            mRecyclerView.setAdapter(moviesAdapter);

        } else {

            errorTextView.setText(getString(R.string.error_message));
            errorTextView.setVisibility(View.VISIBLE);
        }
    }


    //Retrofit methods (for popular and top rated movies)
    @Override
    public void onResponse(Call<Movies> call, Response<Movies> response) {
        List<Movie> movies = response.body().results;

        displayMovies(movies);
    }

    @Override
    public void onFailure(Call<Movies> call, Throwable t) {
        Toast.makeText(MovieListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }


    //loader members (for favorites movies))

    private static final int FAVORITES_LOADER = 0;
    private static final String[] FAVORITES_COLUMNS = {
            MovieColumns.ID,
            MovieColumns.ORIGINAL_TITLE,
            MovieColumns.VOTE_AVERAGE,
            MovieColumns.RELEASE_DATE,
            MovieColumns.OVERVIEW
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_ORIGINAL_TITLE = 1;
    static final int COL_MOVIE_VOTE_AVERAGE = 2;
    static final int COL_MOVIE_RELEASE_DATE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MovieProvider.Movies.CONTENT_URI,
                FAVORITES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<Movie> movies = new ArrayList<>();
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(COL_MOVIE_ID);
                    String originalTitle = cursor.getString(COL_MOVIE_ORIGINAL_TITLE);
                    String voteAverage = cursor.getString(COL_MOVIE_VOTE_AVERAGE);
                    String releaseDate = cursor.getString(COL_MOVIE_RELEASE_DATE);
                    String synopsis = cursor.getString(COL_MOVIE_OVERVIEW);
                    Movie movie = new Movie();
                    movie.setId(id);
                    movie.setOriginalTitle(originalTitle);
                    movie.setUserRating(Double.valueOf(voteAverage));
                    try {
                        movie.setReleaseDate(Utility.sdf.parse(releaseDate));
                    } catch (ParseException e) {
                        Log.e(LOG_TAG, "error parsing date");
                        e.printStackTrace();
                    }
                    movie.setPlotSynopsis(synopsis);
                    movies.add(movie);
                } while (cursor.moveToNext());
            }
        }
        displayMovies(movies);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
