package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.Comentario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ComentarioService {

    @POST("comentario/")
    Call<Comentario> publicar(@Body Comentario comentario);

    @PUT("comentario/")
    Call<Void> editarComentario(@Body Comentario comentario);

    @DELETE("comentario/{id}")
    Call<Void> excluirComentario(@Path("id") long id);

    @GET("comentario/")
    Call<List<Comentario>> buscarTodosComentarios();

    @GET("comentario/{id}")
    Call<Comentario> buscarPorId(@Path("id") long id);

    @GET("comentario/usuario/{id_usuario}")
    Call<List<Comentario>> buscarPorAutor(@Path("id_usuario") long id);

    @GET("comentario/album/{id_album}")
    Call<List<Comentario>> buscarPorAlbum(@Path("id_album") long id);

}