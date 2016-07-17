package com.marnun.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.marnun.popularmoviesapp.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static RecyclerView mRecyclerView;
    private static int mColumnsNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRecyclerView = (RecyclerView) findViewById(R.id.movie_list);
        assert mRecyclerView != null;
        updateMovies();

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
        updateMovies();
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

    private void updateMovies() {

        MoviesTask moviesTask = new MoviesTask();
        moviesTask.execute();

    }

    public class MoviesRecyclerViewAdapter
            extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MoviePosterHolder> {

        private final String LOG_TAG = MoviesRecyclerViewAdapter.class.getSimpleName();
        private final List<Movie> mValues;

        public MoviesRecyclerViewAdapter(List<Movie> items) {
            mValues = items;
        }

        @Override
        public MoviePosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new MoviePosterHolder(view);
        }

        @Override
        public void onBindViewHolder(final MoviePosterHolder holder, int position) {
            holder.mMovie =  mValues.get(position);
            String uri = "http://image.tmdb.org/t/p/w780";
            uri += holder.mMovie.getPosterPath();
            Picasso.with(getBaseContext())
                    .load(uri)
                    .resize(getPosterWidth(), getPosterHeight())
                    .into(holder.mPosterView);
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

        public class MoviePosterHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mPosterView;
            public Movie mMovie;

            public MoviePosterHolder(View view) {
                super(view);
                mView = view;
                mPosterView = (ImageView) view.findViewById(R.id.poster_view);
            }

            @Override
            public String toString() {
                return super.toString(); //+ " '" + mContentView.getText() + "'";
            }
        }
    }

    public class MoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        private final String LOG_TAG = MoviesTask.class.getSimpleName();

        private List<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException, ParseException {

            List<Movie> movies = new ArrayList<>();

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_MOVIES = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_OR_TITLE = "original_title";
            final String OWM_PLOT = "overview";
            final String OWM_RATING = "vote_average";
            final String OWM_REL_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_MOVIES);

            for(int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieObject = moviesArray.getJSONObject(i);

                String title = movieObject.getString(OWM_OR_TITLE);
                String posterPath = movieObject.getString(OWM_POSTER_PATH);
                String plotSynopsis = movieObject.getString(OWM_PLOT);
                Double userRating = movieObject.getDouble(OWM_RATING);
                String releaseDateString = movieObject.getString(OWM_REL_DATE);
                Date releaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(releaseDateString);

                Movie movie = new Movie();
                movie.setPosterPath(posterPath);
                movie.setOriginalTitle(title);
                movie.setPlotSynopsis(plotSynopsis);
                movie.setUserRating(userRating);
                movie.setReleaseDate(releaseDate);

                movies.add(movie);

            }

//            for (Movie movie: movies ) {
//                Log.v(LOG_TAG, movie.toString());
//            }

            return movies;

        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie";

                final String APIKEY_PARAM = "api_key";

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MovieListActivity.this);
                String key = getString(R.string.orders_preference_key);
                String defaultValue = getString(R.string.order_popular_value);
                String sortOrder = sharedPref.getString(key, defaultValue);
//                Log.v(LOG_TAG, "sortOrder: "+sortOrder);

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(sortOrder)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

//            Log.v(LOG_TAG, moviesJsonStr);

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
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

    }

    public static int getPosterWidth() {
        //The width of every poster is calculated dividing the width of the grid by the number of columns
        return mRecyclerView.getWidth() / mColumnsNo;
    }

    public static int getPosterHeight() {
        return (int) (getPosterWidth() * 1.5);
    }
}
