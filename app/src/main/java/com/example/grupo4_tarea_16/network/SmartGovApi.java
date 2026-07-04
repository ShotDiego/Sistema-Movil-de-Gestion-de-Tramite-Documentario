package com.example.grupo4_tarea_16.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SmartGovApi {
    @POST("login")
    Call<Map<String, Object>> login(@Body Map<String, String> body);

    @GET("sincronizacion")
    Call<Map<String, Object>> sincronizacion(@Header("Authorization") String token);

    @POST("sync-data")
    Call<Map<String, Object>> syncData(@Header("Authorization") String token, @Body Map<String, Object> body);

    @GET("{entidad}")
    Call<Map<String, Object>> list(@Header("Authorization") String token, @Path("entidad") String entidad);

    @POST("{entidad}")
    Call<Map<String, Object>> create(@Header("Authorization") String token, @Path("entidad") String entidad, @Body Map<String, Object> body);

    @PUT("{entidad}/{uuid}")
    Call<Map<String, Object>> update(@Header("Authorization") String token, @Path("entidad") String entidad, @Path("uuid") String uuid, @Body Map<String, Object> body);

    @DELETE("{entidad}/{uuid}")
    Call<Map<String, Object>> delete(@Header("Authorization") String token, @Path("entidad") String entidad, @Path("uuid") String uuid);
}
