package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.AvaliacaoLista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AvaliacaoListaService {

    @POST("avaliacao-lista/")
    Call<AvaliacaoLista> cadastrarAvaliacao(@Body AvaliacaoLista avaliacaoLista);

    @PUT("avaliacao-lista/")
    Call<Void> editarAvaliacao(@Body AvaliacaoLista avaliacaoLista);

    @DELETE("avaliacao-lista/{id}")
    Call<Void> excluirAvaliacao(@Path("id") long id);

    @GET("avaliacao-lista/usuario/{id_usuario}")
    Call<List<AvaliacaoLista>> buscarPorUsuario();

    @GET("avaliacao-lista/{id}")
    Call<AvaliacaoLista> buscarPorId(@Path("id") long id);

    @GET("avaliacao-lista/")
    Call<List<AvaliacaoLista>> buscarTodasAvaliacoes();

    @GET("avaliacao-lista/media/{id_lista}")
    Call<Float> calcularMediaLista(@Path("id_lista") long id);

    @GET("avaliacao-lista/lista/{id_lista}")
    Call<List<AvaliacaoLista>> buscarPorLista(@Path("id_lista") long id);

    @GET("avaliacao-lista/{id_lista}/{id_usuario}")
    Call<AvaliacaoLista> buscarPorListaEUsuario(@Path("id_lista") long idLista, @Path("id_usuario") long idUsuario);

}