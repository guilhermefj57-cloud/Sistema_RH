package com.tdsManha.rh.model.dao;

// Substitua pelo seu pacote de conexão real
import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Avaliacao;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {

    // Método auxiliar para mapear um ResultSet para uma entidade Avaliacao
    private Avaliacao mapResultSetToAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(rs.getInt("id"));
        avaliacao.setIdColaborador(rs.getInt("id_colaborador"));
        
        // Converte a String do banco de dados para LocalDate
        String dataStr = rs.getString("data_avaliacao");
        if (dataStr != null) {
            avaliacao.setDataAvaliacao(LocalDate.parse(dataStr));
        }
        
        avaliacao.setNotaDesempenho(rs.getDouble("nota_desempenho"));
        avaliacao.setPlanoAcao(rs.getString("plano_acao"));
        return avaliacao;
    }
    
    // Método para Adicionar (CREATE) uma nova avaliação
    public void addAvaliacao(Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacoes (id_colaborador, data_avaliacao, nota_desempenho, plano_acao) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, avaliacao.getIdColaborador());
            stmt.setString(2, avaliacao.getDataAvaliacao().toString());
            stmt.setDouble(3, avaliacao.getNotaDesempenho());
            stmt.setString(4, avaliacao.getPlanoAcao());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar avaliação: " + e.getMessage());
        }
    }

    /**
     * MÉTODO REQUERIDO PELO CONTROLLER: Atualizar (UPDATE) uma avaliação existente
     */
    public void updateAvaliacao(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacoes SET data_avaliacao = ?, nota_desempenho = ?, plano_acao = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, avaliacao.getDataAvaliacao().toString());
            stmt.setDouble(2, avaliacao.getNotaDesempenho());
            stmt.setString(3, avaliacao.getPlanoAcao());
            stmt.setInt(4, avaliacao.getId()); // Usa o ID para WHERE
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar avaliação: " + e.getMessage());
        }
    }

    /**
     * MÉTODO REQUERIDO PELO CONTROLLER: Excluir (DELETE) uma avaliação
     */
    public void deleteAvaliacao(int id) {
        String sql = "DELETE FROM avaliacoes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar avaliação: " + e.getMessage());
        }
    }

    // Método para Buscar (READ) avaliações por Colaborador
    public List<Avaliacao> getAvaliacoesByColaboradorId(int colaboradorId) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes WHERE id_colaborador = ? ORDER BY data_avaliacao DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, colaboradorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avaliacoes.add(mapResultSetToAvaliacao(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliações por colaborador: " + e.getMessage());
        }
        return avaliacoes;
    }
}