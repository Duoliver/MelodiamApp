package com.example.douglas.melodiam.model;

import com.example.douglas.melodiam.model.Album;
import com.example.douglas.melodiam.model.Avaliacao;
import com.example.douglas.melodiam.model.Usuario;

import java.io.Serializable;

public class AvaliacaoAlbum extends Avaliacao implements Serializable {

	private AlbumServer album;
	
	public AvaliacaoAlbum() {
		this.album = null;
	}
	
	public AvaliacaoAlbum(long idAvaliacao, float avaliacao, Usuario autor, AlbumServer album) {
		super(idAvaliacao, avaliacao, autor);
		this.album = album;
	}


	public AlbumServer getAlbum() {
		return album;
	}

	public void setAlbum(AlbumServer album) {
		this.album = album;
	}

	@Override
	public String toString() {
		return super.getIdAvaliacao()+"\n"+
				super.getAutor()+super.getAvaliacao()+"\n"+
                this.album.getIdSpotify();
	}
	
	
	
}