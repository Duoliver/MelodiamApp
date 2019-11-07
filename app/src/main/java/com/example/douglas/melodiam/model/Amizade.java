package com.example.douglas.melodiam.model;

import java.io.Serializable;

public class Amizade implements Serializable {

	private Usuario usuario1, usuario2;
	private long id;
	private boolean status;
	
	public Amizade(){
		this.usuario1 = null;
		this.usuario2 = null;
	}
	
	public Amizade(Usuario usuario1, Usuario usuario2, long id, boolean status) {
		super();
		this.usuario1 = usuario1;
		this.usuario2 = usuario2;
		this.id = id;
		this.status = status;
	}

	public Usuario getUsuario1() {
		return usuario1;
	}

	public void setUsuario1(Usuario usuario1) {
		this.usuario1 = usuario1;
	}

	public Usuario getUsuario2() {
		return usuario2;
	}

	public void setUsuario2(Usuario usuario2) {
		this.usuario2 = usuario2;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Amizade [usuario1=" + usuario1 + ", usuario2=" + usuario2 + ", id=" + id + ", status=" + status + "]";
	}
	
	
	
}