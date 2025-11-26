package com.tdsManha.rh.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

import com.tdsManha.rh.model.DataModel;
import com.tdsManha.rh.model.dao.FeriasDAO;
import com.tdsManha.rh.model.entity.Colaborador;
import com.tdsManha.rh.model.entity.Ferias;

public class FeriasController implements Initializable {

    @FXML private ComboBox<Colaborador> colaboradorComboBox;
    @FXML private Label saldoLabel;
    @FXML private Label periodoAquisitivoLabel;
    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;
    @FXML private Button solicitarButton;
    @FXML private TableView<Ferias> feriasTableView;
    @FXML private TableColumn<Ferias, LocalDate> dataInicioColumn;
    @FXML private TableColumn<Ferias, LocalDate> dataFimColumn;
    @FXML private TableColumn<Ferias, Integer> diasColumn;
    @FXML private TableColumn<Ferias, Ferias.StatusFerias> statusColumn;

    // Não precisamos mais do ColaboradorDAO aqui
    private final FeriasDAO feriasDAO = new FeriasDAO();
    private Colaborador colaboradorSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarComboBox();
        configurarTabela();

        colaboradorComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    colaboradorSelecionado = newValue;
                    atualizarTela();
                });
    }

    private void configurarComboBox() {
        // A ComboBox agora usa a lista compartilhada do DataModel
        colaboradorComboBox.setItems(DataModel.getInstance().getColaboradores());
    }

    private void configurarTabela() {
        dataInicioColumn.setCellValueFactory(new PropertyValueFactory<>("dataInicio"));
        dataFimColumn.setCellValueFactory(new PropertyValueFactory<>("dataFim"));
        diasColumn.setCellValueFactory(new PropertyValueFactory<>("diasSolicitados"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void atualizarTela() {
        if (colaboradorSelecionado == null) {
            feriasTableView.setItems(null);
            saldoLabel.setText("Saldo Disponível: -");
            periodoAquisitivoLabel.setText("Período Aquisitivo: -");
            return;
        }

        List<Ferias> feriasList = feriasDAO.getFeriasByColaboradorId(colaboradorSelecionado.getId());
        feriasTableView.setItems(FXCollections.observableArrayList(feriasList));

        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = colaboradorSelecionado.getDataAdmissao();
        long anosDeEmpresa = ChronoUnit.YEARS.between(dataAdmissao, hoje);
        LocalDate inicioPeriodoAquisitivo = dataAdmissao.plusYears(anosDeEmpresa);
        LocalDate fimPeriodoAquisitivo = inicioPeriodoAquisitivo.plusYears(1);
        int diasGozados = feriasList.stream()
                .filter(f -> !f.getDataInicio().isBefore(inicioPeriodoAquisitivo))
                .mapToInt(Ferias::getDiasSolicitados)
                .sum();
        int saldo = 30 - diasGozados;

        saldoLabel.setText("Saldo Disponível: " + saldo + " dias");
        periodoAquisitivoLabel.setText("Período Aquisitivo: " + inicioPeriodoAquisitivo + " a " + fimPeriodoAquisitivo);
    }

    @FXML
    private void handleSolicitarButton() {
        if (colaboradorSelecionado == null || dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Preencha todos os campos: Colaborador, Data de Início e Fim.");
            return;
        }

        LocalDate dataInicio = dataInicioPicker.getValue();
        LocalDate dataFim = dataFimPicker.getValue();
        LocalDate hoje = LocalDate.now(); // Adicionando a data de hoje para validação

        // NOVA VALIDAÇÃO: Impedir solicitação no passado
        if (dataInicio.isBefore(hoje)) {
            showAlert(Alert.AlertType.ERROR, "Erro", "A data de início das férias não pode ser anterior à data de hoje.");
            return;
        }

        if (dataFim.isBefore(dataInicio)) {
            showAlert(Alert.AlertType.ERROR, "Erro", "A data de fim não pode ser anterior à data de início.");
            return;
        }

        int diasSolicitados = (int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        int saldoAtual = Integer.parseInt(saldoLabel.getText().replaceAll("[^0-9-]", ""));

        if (diasSolicitados > saldoAtual) {
            showAlert(Alert.AlertType.ERROR, "Erro", "O número de dias solicitados (" + diasSolicitados + ") é maior que o saldo disponível (" + saldoAtual + ").");
            return;
        }

        Ferias novasFerias = new Ferias();
        novasFerias.setIdColaborador(colaboradorSelecionado.getId());
        novasFerias.setDataInicio(dataInicio);
        novasFerias.setDataFim(dataFim);
        novasFerias.setDiasSolicitados(diasSolicitados);
        novasFerias.setStatus(Ferias.StatusFerias.APROVADA);

        feriasDAO.addFerias(novasFerias);
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Férias registradas com sucesso!");
        
        atualizarTela();
        dataInicioPicker.setValue(null);
        dataFimPicker.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}