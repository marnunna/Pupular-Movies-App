package com.marnun.popularmoviesapp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Marco on 03/07/2016.
 */
public class Movie implements Serializable {

    String original_title;
    String poster_path;
    String overview;
    Double vote_average;
    Date release_date;
    String backdrop_path;

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

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("original_title: "+ original_title);
        stringBuilder.append(", ");
        stringBuilder.append("poster_path: "+ poster_path);
        stringBuilder.append(", ");
        stringBuilder.append("overview: "+ overview);
        stringBuilder.append(", ");
        stringBuilder.append("vote_average: "+ vote_average);
        stringBuilder.append(", ");
        stringBuilder.append("release_date: "+ release_date);


        return stringBuilder.toString();
    }
}
