package com.tdsManha.rh.model.entity;

public class Departamento {
    private Integer id;
    private String nome;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Essencial para exibir o nome na ListView e ComboBox
    @Override
    public String toString() {
        return nome;
    }
}