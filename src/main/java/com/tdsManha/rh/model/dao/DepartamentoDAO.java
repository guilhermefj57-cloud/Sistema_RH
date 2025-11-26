package com.tdsManha.rh.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Departamento;

public class DepartamentoDAO {

    public List<Departamento> getAllDepartamentos() {
        String sql = "SELECT * FROM departamentos ORDER BY nome";
        List<Departamento> departamentos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setId(rs.getInt("id"));
                d.setNome(rs.getString("nome"));
                departamentos.add(d);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return departamentos;
    }

    public void addDepartamento(String nome) {
        String sql = "INSERT INTO departamentos(nome) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateDepartamento(Departamento d) {
        String sql = "UPDATE departamentos SET nome = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getNome());
            pstmt.setInt(2, d.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteDepartamento(int id) {
        String sql = "DELETE FROM departamentos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}