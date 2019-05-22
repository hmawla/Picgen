package com.hmawla.picgen.model;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PicApiService {

    @GET("v2/list?limit=5")
    Call<JsonArray> getPicsList();

    @GET("v2/list?limit=5")
    Call<JsonArray> getPicsList(@Query("page") int page);

    @GET("v2/list?}")
    Call<JsonArray> getPicsList(@Query("page") int page, @Query("limit") int limit);

}
