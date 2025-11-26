package com.tdsManha.rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Referência para o TabPane principal
    @FXML
    private TabPane mainTabPane;

    // O JavaFX injeta o controller do FXML incluído se o fx:id for o nome do FXML + "Controller"
    @FXML
    private DashboardController dashboardViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adiciona um "ouvinte" que dispara toda vez que uma nova aba é selecionada
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            // Verifica se a aba recém-selecionada é a de "Início"
            if (newTab != null && newTab.getText().equals("Início")) {
                // Se for, chama o método público de atualização do DashboardController
                System.out.println("Aba Início selecionada. Atualizando dashboard...");
                dashboardViewController.refreshDashboard();
            }
        });
    }
}