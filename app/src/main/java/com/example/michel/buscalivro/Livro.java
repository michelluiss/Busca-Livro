package com.example.michel.buscalivro;


import java.io.Serializable;

public class Livro implements Serializable {

    private String nome;
    private String autor;
    private String classificacao;
    private String estante;
    private String prateleira;
    private String posiX;
    private String posiY;

    public Livro(String nome, String autor, String classificacao, String estante, String prateleira, String posiX, String posiY) {
        this.nome = nome;
        this.autor = autor;
        this.classificacao = classificacao;
        this.estante = estante;
        this.prateleira = prateleira;
        this.posiX = posiX;
        this.posiY = posiY;
    }

    public Livro() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getEstante() {
        return estante;
    }

    public void setEstante(String estante) {
        this.estante = estante;
    }

    public String getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(String prateleira) {
        this.prateleira = prateleira;
    }

    public String getPosiX() {
        return posiX;
    }

    public void setPosiX(String posiX) {
        this.posiX = posiX;
    }

    public String getPosiY() {
        return posiY;
    }

    public void setPosiY(String posiY) {
        this.posiY = posiY;
    }
}
