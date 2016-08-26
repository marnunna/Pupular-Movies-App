package com.marnun.popularmoviesapp.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Marco on 17/08/2016.
 */
public interface ReviewsService {

    String ENDPOINT = "http://api.themoviedb.org";

    @GET("/3/movie/{id}/reviews")
    Call<Reviews> loadReviews(@Path("id") String idMovie, @Query("api_key") String apiKey);

}
