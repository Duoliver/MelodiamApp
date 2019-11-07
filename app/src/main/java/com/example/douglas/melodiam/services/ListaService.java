package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.Lista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ListaService {

    @POST("lista/")
    Call<Lista> cadastrarLista(@Body Lista lista);

    @PUT("lista/")
    Call<Void> editarLista(@Body Lista lista);

    @DELETE("lista/{id}")
    Call<Void> excluir(@Path("id") long id);

    @GET("lista/")
    Call<List<Lista>> buscarTodasListas();

    @GET("lista/id/{id}")
    Call<Lista> buscarPorId(@Path("id") long id);

    @GET("lista/autor/{id_usuario}")
    Call<List<Lista>> buscarPorAutor(@Path("id_usuario") long id);

    @GET("lista/listas/{id_usuario}")
    Call<Integer> retornarNumeroListas(@Path("id_usuario") long idUsuario);

}