package com.example.douglas.melodiam.model;

import java.io.Serializable;

public class Album extends kaaes.spotify.webapi.android.models.Album implements Serializable {

	private String idSpotify;
	private long idAlbum;
	private String nomeAlbum;
	private String artistaAlbum;
	private String imagem;
	private String copy;
	
	public Album() {
		
	}
	
	public Album(String idSpotify, long idAlbum) {
		this.idSpotify = idSpotify;
		this.idAlbum = idAlbum;
	}

	public String getIdSpotify() {
		return idSpotify;
	}

	public void setIdSpotify(String idSpotify) {
		this.idSpotify = idSpotify;
	}

	public long getIdAlbum() {
		return idAlbum;
	}

	public void setIdAlbum(long idAlbum) {
		this.idAlbum = idAlbum;
	}

	public String getNomeAlbum() {
		return nomeAlbum;
	}

	public void setNomeAlbum(String nomeAlbum) {
		this.nomeAlbum = nomeAlbum;
	}

	public String getArtistaAlbum() {
		return artistaAlbum;
	}

	public void setArtistaAlbum(String artistaAlbum) {
		this.artistaAlbum = artistaAlbum;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public String getCopy() {
		return copy;
	}

	public void setCopy(String copy) {
		this.copy = copy;
	}

	@Override
	public String toString() {
		return "Album [idSpotify=" + idSpotify + ", idAlbum=" + idAlbum + "]";
	}
	
	
	
	
}