package com.example.douglas.melodiam.model;

import com.example.douglas.melodiam.model.Avaliacao;
import com.example.douglas.melodiam.model.Lista;

import java.io.Serializable;

public class AvaliacaoLista extends Avaliacao implements Serializable {


	private Lista lista;
	
	public AvaliacaoLista() {
		this.lista = null;
	}


	public AvaliacaoLista(long idAvaliacao, float avaliacao, Usuario autor, Lista lista) {
		super(idAvaliacao, avaliacao, autor);
		this.lista = lista;
	}


	public Lista getLista() {
		return lista;
	}


	public void setLista(Lista lista) {
		this.lista = lista;
	}


	@Override
	public String toString() {
        return super.getIdAvaliacao()+"\n"+
                super.getAutor()+super.getAvaliacao()+"\n"+
                this.lista.getNome();
	}
	
	
	
}