package com.tdsManha.rh.model.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Ferias;

public class FeriasDAO {

    public void addFerias(Ferias ferias) {
        String sql = "INSERT INTO ferias(id_colaborador, data_inicio, data_fim, dias_solicitados, status) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ferias.getIdColaborador());
            pstmt.setString(2, ferias.getDataInicio().toString());
            pstmt.setString(3, ferias.getDataFim().toString());
            pstmt.setInt(4, ferias.getDiasSolicitados());
            pstmt.setString(5, ferias.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Ferias> getFeriasByColaboradorId(int idColaborador) {
        String sql = "SELECT * FROM ferias WHERE id_colaborador = ?";
        List<Ferias> feriasList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idColaborador);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ferias ferias = new Ferias();
                ferias.setId(rs.getInt("id"));
                ferias.setIdColaborador(rs.getInt("id_colaborador"));
                ferias.setDataInicio(LocalDate.parse(rs.getString("data_inicio")));
                ferias.setDataFim(LocalDate.parse(rs.getString("data_fim")));
                ferias.setDiasSolicitados(rs.getInt("dias_solicitados"));
                ferias.setStatus(Ferias.StatusFerias.valueOf(rs.getString("status")));
                feriasList.add(ferias);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return feriasList;
    }

     public List<String> getColaboradoresEmFeriasHoje() {
        String sql = "SELECT c.nome FROM ferias f " +
                     "JOIN colaboradores c ON f.id_colaborador = c.id " +
                     "WHERE date('now', 'localtime') BETWEEN f.data_inicio AND f.data_fim";
        List<String> emFerias = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                emFerias.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return emFerias;
    }
}