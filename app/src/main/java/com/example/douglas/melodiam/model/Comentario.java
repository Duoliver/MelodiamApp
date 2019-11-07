package com.example.douglas.melodiam.model;

import java.io.Serializable;

public class Comentario implements Serializable {

	private long idComentario;
	private Usuario autor;
	private String texto, data;
	private AlbumServer album;
	
	public Comentario() {
		Usuario autor = null;
		Album album = null;
	}
	
	public Comentario(long idComentario, Usuario autor, String texto, String data, AlbumServer album) {
		super();
		this.idComentario = idComentario;
		this.autor = autor;
		this.texto = texto;
		this.data = data;
		this.album = album;
	}

	public long getIdComentario() {
		return idComentario;
	}

	public void setIdComentario(long idComentario) {
		this.idComentario = idComentario;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public AlbumServer getAlbum() {
		return album;
	}

	public void setAlbum(AlbumServer album) {
		this.album = album;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return this.idComentario+" "+this.album.getIdSpotify()+"\n"
				+this.autor+" "+this.data+"\n"
                +this.texto;
	}
	
	
	
}