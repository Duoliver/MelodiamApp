package com.example.douglas.melodiam.model;

public class AlbumServer {

    private long idAlbum;
    private String idSpotify;

    public AlbumServer(){

    }

    public AlbumServer(long idAlbum, String idSpotify) {
        this.idAlbum = idAlbum;
        this.idSpotify = idSpotify;
    }

    public long getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(long idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getIdSpotify() {
        return idSpotify;
    }

    public void setIdSpotify(String idSpotify) {
        this.idSpotify = idSpotify;
    }
}
