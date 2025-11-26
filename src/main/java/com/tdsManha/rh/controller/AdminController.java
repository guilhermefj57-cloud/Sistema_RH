package com.tdsManha.rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.tdsManha.rh.model.DataModel;
import com.tdsManha.rh.model.dao.CargoDAO;
import com.tdsManha.rh.model.dao.DepartamentoDAO;
import com.tdsManha.rh.model.entity.Cargo;
import com.tdsManha.rh.model.entity.Departamento;

public class AdminController implements Initializable {

    // --- Componentes FXML ---
    // Departamentos
    @FXML private ListView<Departamento> departamentoListView;
    @FXML private TextField departamentoField;
    
    // Cargos
    @FXML private ListView<Cargo> cargoListView;
    @FXML private TextField cargoField;

    // --- DAOs e DataModel ---
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private final CargoDAO cargoDAO = new CargoDAO();
    private final DataModel dataModel = DataModel.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vincula as ListViews com as listas observáveis do DataModel
        departamentoListView.setItems(dataModel.getDepartamentos());
        cargoListView.setItems(dataModel.getCargos());

        // Listener para carregar o nome do Departamento selecionado no TextField
        departamentoListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> { 
                    if (newV != null) {
                        departamentoField.setText(newV.getNome());
                    } else {
                        departamentoField.clear(); // Limpa se nada estiver selecionado
                    }
                });

        // Listener para carregar o nome do Cargo selecionado no TextField
        cargoListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> { 
                    if (newV != null) {
                        cargoField.setText(newV.getNome());
                    } else {
                        cargoField.clear(); // Limpa se nada estiver selecionado
                    }
                });
    }

    // ===================================================
    // Lógica para DEPARTAMENTOS
    // ===================================================

    @FXML
    private void handleAddDepartamento() {
        String nome = departamentoField.getText().trim();
        
        if (!nome.isEmpty()) { 
            try {
                departamentoDAO.addDepartamento(nome);
                // Atualiza o DataModel recarregando a lista completa do banco
                dataModel.getDepartamentos().setAll(departamentoDAO.getAllDepartamentos());
                departamentoField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Departamento '" + nome + "' adicionado com sucesso!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível adicionar o departamento. Detalhes: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O nome do departamento não pode estar vazio.");
        }
    }

    @FXML
    private void handleUpdateDepartamento() {
        Departamento selected = departamentoListView.getSelectionModel().getSelectedItem();
        String novoNome = departamentoField.getText().trim(); 
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione um departamento na lista para editar.");
            return;
        }
        
        if (novoNome.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O novo nome do departamento não pode estar vazio.");
            return;
        }

        try {
            selected.setNome(novoNome);
            departamentoDAO.updateDepartamento(selected);
            
            // Apenas atualiza a visualização do item alterado na lista
            departamentoListView.refresh(); 
            departamentoField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Departamento atualizado para '" + novoNome + "'!");
        } catch (Exception e) {
             showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o departamento. Detalhes: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteDepartamento() {
        Departamento selected = departamentoListView.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione um departamento na lista para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação de Exclusão");
        confirm.setHeaderText("Excluir Departamento: " + selected.getNome());
        confirm.setContentText("Esta ação é irreversível. Deseja continuar?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                departamentoDAO.deleteDepartamento(selected.getId());
                // Remove o item da lista observável
                dataModel.getDepartamentos().remove(selected);
                departamentoField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Departamento excluído com sucesso.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir o departamento. Detalhes: " + e.getMessage());
            }
        }
    }

    // ===================================================
    // Lógica para CARGOS
    // ===================================================

    @FXML
    private void handleAddCargo() {
        String nome = cargoField.getText().trim();
        
        if (!nome.isEmpty()) { 
            try {
                cargoDAO.addCargo(nome);
                dataModel.getCargos().setAll(cargoDAO.getAllCargos());
                cargoField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cargo '" + nome + "' adicionado com sucesso!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível adicionar o cargo. Detalhes: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O nome do cargo não pode estar vazio.");
        }
    }

    @FXML
    private void handleUpdateCargo() {
        Cargo selected = cargoListView.getSelectionModel().getSelectedItem();
        String novoNome = cargoField.getText().trim(); 
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione um cargo na lista para editar.");
            return;
        }
        
        if (novoNome.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O novo nome do cargo não pode estar vazio.");
            return;
        }

        try {
            selected.setNome(novoNome);
            cargoDAO.updateCargo(selected);
            cargoListView.refresh(); 
            cargoField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cargo atualizado para '" + novoNome + "'!");
        } catch (Exception e) {
             showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o cargo. Detalhes: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCargo() {
        Cargo selected = cargoListView.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione um cargo na lista para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação de Exclusão");
        confirm.setHeaderText("Excluir Cargo: " + selected.getNome());
        confirm.setContentText("Esta ação é irreversível. Deseja continuar?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                cargoDAO.deleteCargo(selected.getId());
                dataModel.getCargos().remove(selected);
                cargoField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cargo excluído com sucesso.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir o cargo. Detalhes: " + e.getMessage());
            }
        }
    }
    
    // ===================================================
    // Método Auxiliar
    // ===================================================

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}