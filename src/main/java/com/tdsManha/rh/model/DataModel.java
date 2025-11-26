package com.tdsManha.rh.model;

import com.tdsManha.rh.model.dao.CargoDAO;
import com.tdsManha.rh.model.dao.ColaboradorDAO;
import com.tdsManha.rh.model.dao.DepartamentoDAO;
import com.tdsManha.rh.model.entity.Cargo;
import com.tdsManha.rh.model.entity.Colaborador;
import com.tdsManha.rh.model.entity.Departamento;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {

    private static final DataModel instance = new DataModel();

    private final ObservableList<Colaborador> colaboradores;
    private final ObservableList<Departamento> departamentos; // <-- NOVO
    private final ObservableList<Cargo> cargos;             // <-- NOVO

    private DataModel() {
        // Carrega todas as listas uma única vez
        this.colaboradores = FXCollections.observableArrayList(new ColaboradorDAO().getAllColaboradores());
        this.departamentos = FXCollections.observableArrayList(new DepartamentoDAO().getAllDepartamentos());
        this.cargos = FXCollections.observableArrayList(new CargoDAO().getAllCargos());
    }

    public static DataModel getInstance() {
        return instance;
    }

    // Getters para todas as listas
    public ObservableList<Colaborador> getColaboradores() { return colaboradores; }
    public ObservableList<Departamento> getDepartamentos() { return departamentos; }
    public ObservableList<Cargo> getCargos() { return cargos; }
}