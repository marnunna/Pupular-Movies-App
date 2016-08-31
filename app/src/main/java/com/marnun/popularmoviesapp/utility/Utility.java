package com.marnun.popularmoviesapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.marnun.popularmoviesapp.R;
import com.marnun.popularmoviesapp.data.db.MovieColumns;
import com.marnun.popularmoviesapp.data.db.MovieProvider;
import com.marnun.popularmoviesapp.data.model.Movie;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Marco on 25/08/2016.
 */
public class Utility {

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

    public static String getSortCriteria(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String keySortOrder = context.getString(R.string.orders_preference_key);
        String defaultValueSortOrder = context.getString(R.string.order_popular_value);
        return sharedPref.getString(keySortOrder, defaultValueSortOrder);
    }

    public static boolean isFavorite(Context context, Movie movie) {
        String id = movie.getId();
        Cursor cursor = context.getContentResolver().query(MovieProvider.Movies.withId(id), new String[] {MovieColumns.ID}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
