package com.example.douglas.melodiam.model;

import java.io.Serializable;

public class AlbumLista implements Serializable {
	
	private long idAlbumLista;
	private AlbumServer album;
	private Lista lista;
	
	public AlbumLista(){
	}

	public AlbumLista(long idAlbumLista, AlbumServer album, Lista lista) {
		super();
		this.album = album;
		this.lista = lista;
		this.idAlbumLista = idAlbumLista;
	}

	public AlbumServer getAlbum() {
		return album;
	}

	public void setAlbum(AlbumServer album) {
		this.album = album;
	}

	public Lista getLista() {
		return lista;
	}

	public void setLista(Lista lista) {
		this.lista = lista;
	}

	public long getIdAlbumLista() {
		return idAlbumLista;
	}

	public void setIdAlbumLista(long idAlbumLista) {
		this.idAlbumLista = idAlbumLista;
	}

	@Override
	public String toString() {
		return "AlbumLista [idAlbumLista=" + idAlbumLista + ", album=" + album + ", lista=" + lista + "]";
	}
	
	
	
}