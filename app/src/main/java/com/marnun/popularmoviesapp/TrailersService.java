package com.marnun.popularmoviesapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Marco on 17/08/2016.
 */
public interface TrailersService {

    String ENDPOINT = "http://api.themoviedb.org";

    @GET("/3/movie/{id}/videos")
    Call<Trailers> loadTrailers(@Path("id") String idMovie, @Query("api_key") String apiKey);

}
