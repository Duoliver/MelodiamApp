package com.example.douglas.melodiam.services;

import com.example.douglas.melodiam.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {

    @POST("usuario/")
    Call<Usuario> cadastrarUsuario(@Body Usuario usuario);

    @PUT("usuario/")
    Call<Void> editarUsuario(@Body Usuario usuario);

    @DELETE("usuario/{id}")
    Call<Void> excluirUsuario(@Path("id") long id);

    @GET("usuario/")
    Call<List<Usuario>> listaTodosUsuarios();

    @GET("usuario/{id}")
    Call<Usuario> buscarPorId(@Path("id") long id);

    @GET("usuario/login/{login}")
    Call<Usuario> buscarPorLogin(@Path("login") String login);

    @GET("usuario/{login}/{senha}")
    Call<Usuario> buscarPorLoginESenha(@Path("login") String login, @Path("senha") String senha);

}