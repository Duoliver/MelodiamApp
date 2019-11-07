package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.AmizadeLista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AmizadeListaService {

    @POST("amizade-lista/")
    Call<AmizadeLista> compartilhar(@Body AmizadeLista amizadeLista);

    @GET("amizade-lista/")
    Call<List<AmizadeLista>> buscarTodos();

    @GET("amizade-lista/recebidos/{id_usuario}")
    Call<List<AmizadeLista>> buscarRecebidosPorUsuario(@Path("id_usuario") long id);

    @GET("amizade-lista/{id_autor}/{id_leitor}")
    Call<List<AmizadeLista>> buscarPorAutorELeitor(@Path("id_autor") long idAutor, @Path("id_leitor") long idLeitor);

    @GET("amizade-lista/compartilhados/{id_usuario}")
    Call<List<AmizadeLista>> buscarCompartilhadosPorUsuario(@Path("id_usuario") long id);

}