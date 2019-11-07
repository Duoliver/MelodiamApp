package com.example.douglas.melodiam.model;

import java.io.Serializable;

public class Lista implements Serializable {

	private long idLista;
	private Usuario autor;
	private String nome, descricao;

	
	public Lista() {
		this.autor = null;
	}

	public Lista(long idLista, Usuario autor, String nome, String descricao) {
		super();
		this.idLista = idLista;
		this.autor = autor;
		this.nome = nome;
		this.descricao = descricao;
	}

	public long getIdLista() {
		return idLista;
	}

	public void setIdLista(long idLista) {
		this.idLista = idLista;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return this.nome;
	}

}