package com.tdsManha.rh.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition; // Adicionado para auxiliar na limpeza do R$
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import com.tdsManha.rh.model.DataModel;
import com.tdsManha.rh.model.dao.ColaboradorDAO;
import com.tdsManha.rh.model.entity.Cargo;
import com.tdsManha.rh.model.entity.Colaborador;
import com.tdsManha.rh.model.entity.Departamento;

public class ColaboradorController implements Initializable {

    // Componentes da Tabela
    @FXML private TableView<Colaborador> colaboradorTableView;
    @FXML private TableColumn<Colaborador, Integer> idColumn;
    @FXML private TableColumn<Colaborador, String> nomeColumn;
    @FXML private TableColumn<Colaborador, String> departamentoColumn; 
    @FXML private TableColumn<Colaborador, String> cargoColumn; 
    @FXML private TableColumn<Colaborador, LocalDate> dataAdmissaoColumn;
    @FXML private TableColumn<Colaborador, Double> salarioColumn;
    @FXML private TableColumn<Colaborador, Colaborador.Status> statusColumn;

    // Componentes do Formulário
    @FXML private TextField nomeField;
    @FXML private DatePicker dataAdmissaoPicker;
    @FXML private TextField salarioField;
    @FXML private ComboBox<Departamento> departamentoComboBox;
    @FXML private ComboBox<Cargo> cargoComboBox;
    @FXML private ComboBox<Colaborador.Status> statusComboBox;

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final DataModel dataModel = DataModel.getInstance();
    
    // Adicionado um DecimalFormat para evitar a notação científica (o "E")
    private static final DecimalFormat FORMATADOR_SALARIO_EXIBICAO = 
        new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configura as colunas da tabela
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        departamentoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeDepartamento"));
        cargoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeCargo"));
        dataAdmissaoColumn.setCellValueFactory(new PropertyValueFactory<>("dataAdmissao"));
        salarioColumn.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Adiciona um formatador de célula para o salário na tabela
        salarioColumn.setCellFactory(tc -> new TableCell<Colaborador, Double>() {
            @Override
            protected void updateItem(Double salario, boolean empty) {
                super.updateItem(salario, empty);
                if (empty || salario == null) {
                    setText(null);
                } else {
                    // **ALTERAÇÃO AQUI: Adiciona o prefixo "R$ "**
                    setText("R$ " + FORMATADOR_SALARIO_EXIBICAO.format(salario));
                }
            }
        });

        // Popula as ComboBoxes com dados do DataModel
        departamentoComboBox.setItems(dataModel.getDepartamentos());
        cargoComboBox.setItems(dataModel.getCargos());
        statusComboBox.setItems(FXCollections.observableArrayList(Colaborador.Status.values()));

        // Define a tabela para usar a lista principal de colaboradores
        colaboradorTableView.setItems(dataModel.getColaboradores());

        // Listener para preencher o formulário ao selecionar na tabela
        colaboradorTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> showColaboradorDetails(newV));
    }

    private void showColaboradorDetails(Colaborador c) {
        if (c != null) {
            nomeField.setText(c.getNome());
            dataAdmissaoPicker.setValue(c.getDataAdmissao());
            
            // **ALTERAÇÃO AQUI: Adiciona o prefixo "R$ " ao preencher o campo de texto**
            salarioField.setText("R$ " + FORMATADOR_SALARIO_EXIBICAO.format(c.getSalarioBase())); 
            
            statusComboBox.setValue(c.getStatus());

            // Lógica para selecionar o item correto nas ComboBoxes
            departamentoComboBox.getSelectionModel().select(
                dataModel.getDepartamentos().stream()
                        .filter(d -> d.getId().equals(c.getIdDepartamento()))
                        .findFirst().orElse(null)
            );
            cargoComboBox.getSelectionModel().select(
                dataModel.getCargos().stream()
                        .filter(cargo -> cargo.getId().equals(c.getIdCargo()))
                        .findFirst().orElse(null)
            );

        } else {
            handleClearButton();
        }
    }

    @FXML
    private void handleSaveButton() {
        // Validações básicas e para evitar apenas espaços em branco
        if (nomeField.getText().trim().isEmpty() || dataAdmissaoPicker.getValue() == null || salarioField.getText().trim().isEmpty() ||
            departamentoComboBox.getValue() == null || cargoComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Por favor, preencha todos os campos e evite preenchê-los apenas com espaços.");
            return;
        }

        Colaborador selectedColaborador = colaboradorTableView.getSelectionModel().getSelectedItem();

        try {
            String salarioTexto = salarioField.getText();
            
            // REMOVE "R$ " E TENTA CONVERTER O TEXTO LIMPO PARA DOUBLE
            String salarioLimpo = salarioTexto.replace("R$", "").trim();
            
            DecimalFormat formatadorEntrada = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
            formatadorEntrada.setParseBigDecimal(true);

            // Converte o texto formatado (ex: "1.000,50") para um Double
            double salario = formatadorEntrada.parse(salarioLimpo).doubleValue(); 

            if (selectedColaborador == null) { // Novo Colaborador
                Colaborador newColaborador = new Colaborador();
                setColaboradorFromFields(newColaborador, salario);
                colaboradorDAO.addColaborador(newColaborador);
            } else { // Atualizar Colaborador
                setColaboradorFromFields(selectedColaborador, salario);
                colaboradorDAO.updateColaborador(selectedColaborador);
            }

            // Recarrega todos os dados para garantir consistência
            dataModel.getColaboradores().setAll(colaboradorDAO.getAllColaboradores());
            handleClearButton();

        } catch (Exception e) { // Captura NumberFormatException e ParseException
            // Melhorar a mensagem para refletir que o R$ não é o problema
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Por favor, insira um valor numérico válido para o salário. Ex: 1200,50 (sem o R$ ou pontos de milhar).");
        }
    }

    // Método auxiliar para evitar repetição de código
    private void setColaboradorFromFields(Colaborador c, double salario) {
        c.setNome(nomeField.getText().trim());
        c.setDataAdmissao(dataAdmissaoPicker.getValue());
        c.setSalarioBase(salario);
        c.setStatus(statusComboBox.getValue());
        c.setIdDepartamento(departamentoComboBox.getValue().getId());
        c.setIdCargo(cargoComboBox.getValue().getId());
    }

    @FXML
    private void handleDeleteButton() {
        Colaborador selected = colaboradorTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            colaboradorDAO.deleteColaborador(selected.getId());
            dataModel.getColaboradores().remove(selected);
            handleClearButton();
        }
    }

    @FXML
    private void handleClearButton() {
        colaboradorTableView.getSelectionModel().clearSelection();
        nomeField.clear();
        dataAdmissaoPicker.setValue(null);
        salarioField.clear();
        departamentoComboBox.setValue(null);
        cargoComboBox.setValue(null);
        statusComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}