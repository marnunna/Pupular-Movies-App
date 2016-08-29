package com.marnun.popularmoviesapp;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.marnun.popularmoviesapp.data.db.MovieColumns;
import com.marnun.popularmoviesapp.data.db.MovieProvider;
import com.marnun.popularmoviesapp.data.model.Movie;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testContentProvider() throws Exception {

        ContentValues values = new ContentValues();
        values.put(MovieColumns.ID, "1");
        values.put(MovieColumns.ORIGINAL_TITLE, "Il glandiatore");
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.insert(MovieProvider.Movies.CONTENT_URI, values);
        Cursor cursor = contentResolver.query(MovieProvider.Movies.CONTENT_URI, null, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(MovieColumns.ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieColumns.ORIGINAL_TITLE));
                Movie movie = new Movie();
                movie.setId(id);
                movie.setOriginalTitle(title);
                Log.d("Content provider test", movie.getId() + " " + movie.getOriginalTitle());
            }
            cursor.close();
        }
        contentResolver.delete(MovieProvider.Movies.CONTENT_URI, null, null);
    }
}