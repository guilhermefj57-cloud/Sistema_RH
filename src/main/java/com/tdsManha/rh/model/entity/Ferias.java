package com.tdsManha.rh.model.entity;

import java.time.LocalDate;

public class Ferias {

    public enum StatusFerias {
        APROVADA, PENDENTE, REPROVADA
    }

    private Integer id;
    private Integer idColaborador;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer diasSolicitados;
    private StatusFerias status;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdColaborador() { return idColaborador; }
    public void setIdColaborador(Integer idColaborador) { this.idColaborador = idColaborador; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public Integer getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(Integer diasSolicitados) { this.diasSolicitados = diasSolicitados; }
    public StatusFerias getStatus() { return status; }
    public void setStatus(StatusFerias status) { this.status = status; }
}