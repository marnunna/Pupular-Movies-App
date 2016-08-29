package com.marnun.popularmoviesapp.data.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(
        authority = MovieProvider.AUTHORITY,
        database = MovieDatabase.class,
        packageName = "com.marnun.popularmoviesapp.data.db.generated")
public final class MovieProvider {

    public static final String AUTHORITY =
            "com.marnun.popularmoviesapp.data.db.MovieProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String MOVIES = "movies";
    }

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/planet")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/planet",
                whereColumn = MovieColumns.ID,
                pathSegment = 1)
        public static Uri withId(String id) {
            return buildUri(Path.MOVIES, id);
        }
    }

}
