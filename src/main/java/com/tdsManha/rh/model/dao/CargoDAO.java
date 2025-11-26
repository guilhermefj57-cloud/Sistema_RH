package com.tdsManha.rh.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Cargo;

public class CargoDAO {

    public List<Cargo> getAllCargos() {
        String sql = "SELECT * FROM cargos ORDER BY nome";
        List<Cargo> cargos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cargo c = new Cargo();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                cargos.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cargos;
    }

    public void addCargo(String nome) {
        String sql = "INSERT INTO cargos(nome) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCargo(Cargo c) {
        String sql = "UPDATE cargos SET nome = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setInt(2, c.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCargo(int id) {
        String sql = "DELETE FROM cargos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}