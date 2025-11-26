package com.tdsManha.rh.model.entity;

import java.time.LocalDate;
    
public class Avaliacao {

    private Integer id;
    private Integer idColaborador;
    private LocalDate dataAvaliacao;
    private Double notaDesempenho;
    private String planoAcao;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdColaborador() { return idColaborador; }
    public void setIdColaborador(Integer idColaborador) { this.idColaborador = idColaborador; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    public Double getNotaDesempenho() { return notaDesempenho; }
    public void setNotaDesempenho(Double notaDesempenho) { this.notaDesempenho = notaDesempenho; }
    public String getPlanoAcao() { return planoAcao; }
    public void setPlanoAcao(String planoAcao) { this.planoAcao = planoAcao; }
}