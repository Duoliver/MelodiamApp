package com.example.douglas.melodiam.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {
	
	private long idUsuario;
	private String login;
	private String senha;
	
	public Usuario() {
	}
	
	public Usuario(long idUsuario, String login, String senha) {
		super();
		this.idUsuario = idUsuario;
		this.login = login;
		this.senha = senha;
	}

	public long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha){
		this.senha = senha;
	}
	
	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", login=" + login + ", senha=" + senha + "]";
	}
	
	
	
}