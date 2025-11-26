package com.tdsManha.rh.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.tdsManha.rh.model.DataModel;
import com.tdsManha.rh.model.dao.AvaliacaoDAO;
import com.tdsManha.rh.model.entity.Avaliacao;
import com.tdsManha.rh.model.entity.Colaborador;

public class AvaliacaoController implements Initializable {

    @FXML private ComboBox<Colaborador> colaboradorComboBox;
    @FXML private DatePicker dataAvaliacaoPicker;
    @FXML private Spinner<Double> notaSpinner;
    @FXML private TextArea planoAcaoTextArea;
    @FXML private Button salvarButton;
    // NOVOS COMPONENTES FXML
    @FXML private Button editarButton;
    @FXML private Button excluirButton;
    
    @FXML private TableView<Avaliacao> avaliacaoTableView;
    @FXML private TableColumn<Avaliacao, LocalDate> dataColumn;
    @FXML private TableColumn<Avaliacao, Double> notaColumn;
    @FXML private TableColumn<Avaliacao, String> planoAcaoColumn;

    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private Colaborador colaboradorSelecionado;
    // NOVO CAMPO: Para rastrear a avaliação selecionada na tabela
    private Avaliacao avaliacaoSelecionada; 

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarComponentes();
        
        // Listener para carregamento dos dados do colaborador
        colaboradorComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    colaboradorSelecionado = newValue;
                    atualizarTabela();
                    limparCampos();
                    this.avaliacaoSelecionada = null;
                });
                
        // NOVO: Listener para seleção da tabela (carrega dados para edição/contexto)
        avaliacaoTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> carregarAvaliacaoParaEdicao(newValue));
    }

    private void configurarComponentes() {
        colaboradorComboBox.setItems(DataModel.getInstance().getColaboradores());

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 5.0, 3.0, 0.5);
        notaSpinner.setValueFactory(valueFactory);

        dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataAvaliacao"));
        notaColumn.setCellValueFactory(new PropertyValueFactory<>("notaDesempenho"));
        planoAcaoColumn.setCellValueFactory(new PropertyValueFactory<>("planoAcao"));
    }

    private void atualizarTabela() {
        if (colaboradorSelecionado != null) {
            avaliacaoTableView.setItems(FXCollections.observableArrayList(
                    avaliacaoDAO.getAvaliacoesByColaboradorId(colaboradorSelecionado.getId())
            ));
        } else {
            avaliacaoTableView.setItems(null);
        }
    }
    
    // NOVO: Método para carregar a avaliação selecionada para edição
    private void carregarAvaliacaoParaEdicao(Avaliacao avaliacao) {
        this.avaliacaoSelecionada = avaliacao;
        if (avaliacao != null) {
            dataAvaliacaoPicker.setValue(avaliacao.getDataAvaliacao());
            notaSpinner.getValueFactory().setValue(avaliacao.getNotaDesempenho());
            planoAcaoTextArea.setText(avaliacao.getPlanoAcao());
            
            // Altera o texto do botão Salvar para "Atualizar"
            salvarButton.setText("Atualizar Avaliação");
        } else {
            limparCampos();
            salvarButton.setText("Salvar Avaliação");
        }
    }

    @FXML
    private void handleSalvarButton() {
        String planoAcao = planoAcaoTextArea.getText().trim();
        
        // 1. Validação de campos obrigatórios
        if (colaboradorSelecionado == null || dataAvaliacaoPicker.getValue() == null || planoAcao.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Preencha o Colaborador, a Data e o Plano de Ação.");
            return;
        }

        // 2. Verifica se estamos atualizando ou salvando
        boolean isUpdate = (avaliacaoSelecionada != null && avaliacaoSelecionada.getId() > 0);
        
        Avaliacao avaliacao;
        if (isUpdate) {
            avaliacao = avaliacaoSelecionada; 
        } else {
            avaliacao = new Avaliacao();
        }

        // 3. Define os valores
        avaliacao.setIdColaborador(colaboradorSelecionado.getId());
        avaliacao.setDataAvaliacao(dataAvaliacaoPicker.getValue());
        avaliacao.setNotaDesempenho(notaSpinner.getValue());
        avaliacao.setPlanoAcao(planoAcao);

        // 4. Executa a ação
        if (isUpdate) {
            avaliacaoDAO.updateAvaliacao(avaliacao);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Avaliação atualizada com sucesso!");
        } else {
            avaliacaoDAO.addAvaliacao(avaliacao);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Avaliação registrada com sucesso!");
        }

        atualizarTabela();
        limparCampos();
        this.avaliacaoSelecionada = null; // Limpa a seleção após salvar/atualizar
    }

    // NOVO: Método de Edição (apenas carrega para o formulário, a atualização é feita pelo handleSalvarButton)
    @FXML
    private void handleEditarButton() {
        Avaliacao selecionada = avaliacaoTableView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            carregarAvaliacaoParaEdicao(selecionada);
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione uma avaliação na tabela para editar.");
        }
    }

    // NOVO: Método de Exclusão
    @FXML
    private void handleExcluirButton() {
        Avaliacao avaliacao = avaliacaoTableView.getSelectionModel().getSelectedItem();
        if (avaliacao != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                                      "Tem certeza que deseja excluir esta avaliação?", 
                                      ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmação de Exclusão");
            
            if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                avaliacaoDAO.deleteAvaliacao(avaliacao.getId());
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Avaliação excluída com sucesso!");
                
                // Limpa e atualiza
                limparCampos();
                this.avaliacaoSelecionada = null;
                salvarButton.setText("Salvar Avaliação");
                atualizarTabela();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione uma avaliação na tabela para excluir.");
        }
    }

    private void limparCampos() {
        dataAvaliacaoPicker.setValue(null);
        notaSpinner.getValueFactory().setValue(3.0);
        planoAcaoTextArea.clear();
        this.avaliacaoSelecionada = null;
        salvarButton.setText("Salvar Avaliação"); // Garante que o texto volte ao padrão
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}