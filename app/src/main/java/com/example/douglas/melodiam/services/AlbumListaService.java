package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.AlbumLista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AlbumListaService {

    @POST("album-lista/")
    Call<AlbumLista> inserirEmLista(@Body AlbumLista albumLista);

    @DELETE("album-lista/{id}")
    Call<Void> excluirDaLista(@Path("id") long id);

    @GET("album-lista/lista/{id_lista}")
    Call<List<AlbumLista>> buscarPorLista(@Path("id_lista") long id);

}