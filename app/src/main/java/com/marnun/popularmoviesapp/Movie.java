package com.marnun.popularmoviesapp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by Marco on 03/07/2016.
 */
public class Movie implements Serializable {

    String id;
    String original_title;
    String poster_path;
    String overview;
    Double vote_average;
    Date release_date;
    String backdrop_path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.original_title = originalTitle;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String posterPath) {
        this.poster_path = posterPath;
    }

    public String getPlotSynopsis() {
        return overview;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.overview = plotSynopsis;
    }

    public Double getUserRating() {
        return vote_average;
    }

    public void setUserRating(Double userRating) {
        this.vote_average = userRating;
    }

    public Date getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(Date releaseDate) {
        this.release_date = releaseDate;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public void setBackdropPath(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);
        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();
        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");
        return result.toString();
    }
}
