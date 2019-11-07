package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.Album;
import com.example.douglas.melodiam.model.AlbumServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AlbumService {

    @POST("album/")
    Call<AlbumServer> cadastrarAlbum(@Body AlbumServer album);

    @GET("album/")
    Call<List<AlbumServer>> buscarTodosAlbums();

    @GET("album/{id}")
    Call<AlbumServer> buscarPorId(@Path("id") long id);

    @GET("album/spotify/{id_spotify}")
    Call<AlbumServer> buscarIdSpotify(@Path("id_spotify") String idSpotify);

}