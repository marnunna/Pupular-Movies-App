package com.marnun.popularmoviesapp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Marco on 03/07/2016.
 */
public class Movie implements Serializable {

    private String originalTitle;
    private String posterPath;
    private String plotSynopsis;
    private Double userRating;
    private Date releaseDate;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("originalTitle: "+ originalTitle);
        stringBuilder.append(", ");
        stringBuilder.append("posterPath: "+posterPath);
        stringBuilder.append(", ");
        stringBuilder.append("plotSynopsis: "+plotSynopsis);
        stringBuilder.append(", ");
        stringBuilder.append("userRating: "+userRating);
        stringBuilder.append(", ");
        stringBuilder.append("releaseDate: "+releaseDate);


        return stringBuilder.toString();
    }
}
