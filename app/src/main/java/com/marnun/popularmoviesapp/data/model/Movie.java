package com.marnun.popularmoviesapp.data.model;

import java.io.Serializable;
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
        return "Movie{" +
                "id='" + id + '\'' +
                ", original_title='" + original_title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", overview='" + overview + '\'' +
                ", vote_average=" + vote_average +
                ", release_date=" + release_date +
                ", backdrop_path='" + backdrop_path + '\'' +
                '}';
    }
}
