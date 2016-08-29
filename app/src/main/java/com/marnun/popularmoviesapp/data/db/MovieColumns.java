package com.marnun.popularmoviesapp.data.db;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by sam_chordas on 8/11/15.
 */
public interface MovieColumns {

    @DataType(DataType.Type.TEXT) @PrimaryKey
    public static final String ID = "id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String ORIGINAL_TITLE = "original_title";
    @DataType(DataType.Type.TEXT)
    public static final String OVERVIEW = "overview";
    @DataType(DataType.Type.REAL)
    public static final String VOTE_AVERAGE = "vote_average";
    @DataType(DataType.Type.TEXT)
    public static final String RELEASE_DATE = "release_date";

}

