package com.marnun.popularmoviesapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.marnun.popularmoviesapp.R;

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

}
