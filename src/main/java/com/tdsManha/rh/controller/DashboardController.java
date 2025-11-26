package com.tdsManha.rh.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Node; 
import javafx.scene.control.Tooltip; // Importação necessária
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox; // Importação necessária para exemplo de legenda customizada

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.tdsManha.rh.model.dao.ColaboradorDAO;
import com.tdsManha.rh.model.dao.FeriasDAO;
import com.tdsManha.rh.model.entity.Colaborador;

public class DashboardController implements Initializable {

    @FXML private Label totalColaboradoresLabel;
    @FXML private Label ativosLabel;
    @FXML private Label inativosLabel;
    @FXML private PieChart departamentoPieChart;
    @FXML private ListView<String> aniversariantesListView;
    @FXML private ListView<String> feriasListView;

    // Se você tiver um contêiner específico para a legenda no FXML, poderia ser referenciado aqui
    // @FXML private VBox legendaContainer; 

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final FeriasDAO feriasDAO = new FeriasDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshDashboard();
    }

    public void refreshDashboard() {
        loadMetricas();
        loadAniversariantes();
        loadFerias();
        loadPieChart();
    }

    private void loadMetricas() {
        totalColaboradoresLabel.setText(String.valueOf(colaboradorDAO.countTotalColaboradores()));
        ativosLabel.setText(String.valueOf(colaboradorDAO.countColaboradoresPorStatus(Colaborador.Status.ATIVO)));
        inativosLabel.setText(String.valueOf(colaboradorDAO.countColaboradoresPorStatus(Colaborador.Status.INATIVO)));
    }

    private void loadAniversariantes() {
        aniversariantesListView.setItems(FXCollections.observableArrayList(colaboradorDAO.getProximosAniversariantes()));
    }

    private void loadFerias() {
        feriasListView.setItems(FXCollections.observableArrayList(feriasDAO.getColaboradoresEmFeriasHoje()));
    }

    private void loadPieChart() {
        Map<String, Integer> contagem = colaboradorDAO.getContagemPorDepartamento();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        departamentoPieChart.getData().clear(); // Limpar dados antigos

        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue())); // Adiciona contagem ao nome
        }

        departamentoPieChart.setData(pieChartData);
        departamentoPieChart.setTitle("Colaboradores por Departamento");
        
        // Desativa os rótulos visíveis no gráfico para evitar sobreposição interna
        departamentoPieChart.setLabelLineLength(0); // Garante que as linhas dos rótulos não apareçam
        departamentoPieChart.setLabelsVisible(false); // Desativa os rótulos diretamente no gráfico
        
        // Configura Tooltips para cada fatia do gráfico
        // Isso permite ver o detalhe do departamento e a contagem ao passar o mouse.
        for (PieChart.Data data : departamentoPieChart.getData()) {
            // Remove o texto extra " (X)" para o Tooltip, se preferir
            String originalName = data.getName().replaceAll(" \\(\\d+\\)$", ""); 
            Tooltip tooltip = new Tooltip(originalName + ": " + data.getPieValue() + " colaboradores");
            Tooltip.install(data.getNode(), tooltip);
            
            // Opcional: Adicionar um estilo para o nó da fatia, se quiser mudar a cor, etc.
            // data.getNode().setStyle("-fx-pie-color: #..."); 
        }

        // Garante que a legenda esteja visível
        departamentoPieChart.setLegendVisible(true);
        // Tenta posicionar a legenda à direita para dar mais espaço na horizontal
        // Isso pode ou não funcionar dependendo do layout pai no FXML.
        departamentoPieChart.setLegendSide(javafx.geometry.Side.RIGHT); 
        
        departamentoPieChart.setAnimated(true);
    }
}