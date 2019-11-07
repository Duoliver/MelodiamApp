package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.AvaliacaoAlbum;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AvaliacaoAlbumService {

    @POST("avaliacao-album/")
    Call<AvaliacaoAlbum> cadastrarAvaliacao(@Body AvaliacaoAlbum avaliacaoAlbum);

    @PUT("avaliacao-album/")
    Call<Void> editarAvaliacao(@Body AvaliacaoAlbum avaliacaoAlbum);

    @DELETE("avaliacao-album/{id}")
    Call<Void> excluirAvaliacao(@Path("id") long id);

    @GET("avaliacao-album/usuario/{id_usuario}")
    Call<List<AvaliacaoAlbum>> buscarPorUsuario();

    @GET("avaliacao-album/{id}")
    Call<AvaliacaoAlbum> buscarPorId(@Path("id") long id);

    @GET("avaliacao-album/")
    Call<List<AvaliacaoAlbum>> buscarTodasAvaliacoes();

    @GET("avaliacao-album/media/{id_album}")
    Call<Float> calcularMediaAlbum(@Path("id_album") long id);

    @GET("avaliacao-album/album/{id_album}")
    Call<AvaliacaoAlbum> buscarPorAlbum(@Path("id_album") long id);

    @GET("avaliacao-album/album/usuario/{id_album}/{id_usuario}")
    Call<AvaliacaoAlbum> buscarPorAlbumEUsuario(@Path("id_album") long idAlbum, @Path("id_usuario") long idUsuario);



}