package com.marnun.popularmoviesapp.data.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION,
        packageName = "com.marnun.popularmoviesapp.data.db.generated")
public final class MovieDatabase {

    public static final int VERSION = 1;

    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";

    private MovieDatabase() {

    }

}
