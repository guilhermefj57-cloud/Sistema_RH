package com.tdsManha.rh.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Usuario;

public class UsuarioDAO {

    public boolean addUser(Usuario usuario) {
        // Em um app real, a senha seria hasheada aqui antes de salvar
        String sql = "INSERT INTO usuarios(nome, email, senha) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar usuário: " + e.getMessage());
            return false;
        }
    }

    public Usuario checkLogin(String email, String senha) {
        // Em um app real, você buscaria o usuário pelo email e depois compararia a senha hasheada
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, senha);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                return usuario;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar login: " + e.getMessage());
        }
        return null; // Retorna nulo se o login falhar
    }

    public boolean findUserByEmail(String email) {
        String sql = "SELECT id FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Retorna true se encontrou um usuário
        } catch (SQLException e) {
            System.out.println("Erro ao buscar email: " + e.getMessage());
        }
        return false;
    }
    
}