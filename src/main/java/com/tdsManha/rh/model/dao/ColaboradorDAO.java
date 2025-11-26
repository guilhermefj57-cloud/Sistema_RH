package com.tdsManha.rh.model.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tdsManha.rh.database.DatabaseConnection;
import com.tdsManha.rh.model.entity.Colaborador;

public class ColaboradorDAO {

    public List<Colaborador> getAllColaboradores() {
        String sql = "SELECT c.id, c.nome, c.data_admissao, c.salario_base, c.status, " +
                     "d.id as id_departamento, d.nome as nome_departamento, " +
                     "ca.id as id_cargo, ca.nome as nome_cargo " +
                     "FROM colaboradores c " +
                     "LEFT JOIN departamentos d ON c.id_departamento = d.id " +
                     "LEFT JOIN cargos ca ON c.id_cargo = ca.id";
        List<Colaborador> colaboradores = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Colaborador colab = new Colaborador();
                colab.setId(rs.getInt("id"));
                colab.setNome(rs.getString("nome"));
                colab.setDataAdmissao(LocalDate.parse(rs.getString("data_admissao")));
                colab.setSalarioBase(rs.getDouble("salario_base"));
                colab.setStatus(Colaborador.Status.valueOf(rs.getString("status")));
                colab.setIdDepartamento(rs.getInt("id_departamento"));
                colab.setNomeDepartamento(rs.getString("nome_departamento"));
                colab.setIdCargo(rs.getInt("id_cargo"));
                colab.setNomeCargo(rs.getString("nome_cargo"));
                colaboradores.add(colab);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return colaboradores;
    }

    public void addColaborador(Colaborador c) {
        String sql = "INSERT INTO colaboradores(nome, data_admissao, salario_base, status, id_departamento, id_cargo) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, c.getDataAdmissao().toString());
            pstmt.setDouble(3, c.getSalarioBase());
            pstmt.setString(4, c.getStatus().toString());
            pstmt.setInt(5, c.getIdDepartamento());
            pstmt.setInt(6, c.getIdCargo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateColaborador(Colaborador c) {
        String sql = "UPDATE colaboradores SET nome = ?, data_admissao = ?, salario_base = ?, status = ?, id_departamento = ?, id_cargo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, c.getDataAdmissao().toString());
            pstmt.setDouble(3, c.getSalarioBase());
            pstmt.setString(4, c.getStatus().toString());
            pstmt.setInt(5, c.getIdDepartamento());
            pstmt.setInt(6, c.getIdCargo());
            pstmt.setInt(7, c.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteColaborador(int id) {
        String sql = "DELETE FROM colaboradores WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

       public int countTotalColaboradores() {
        String sql = "SELECT COUNT(*) FROM colaboradores";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int countColaboradoresPorStatus(Colaborador.Status status) {
        String sql = "SELECT COUNT(*) FROM colaboradores WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public List<String> getProximosAniversariantes() {
        // Retorna colaboradores cujo aniversário de empresa ocorre nos próximos 30 dias
        String sql = "SELECT nome, data_admissao FROM colaboradores " +
                     "WHERE strftime('%m-%d', data_admissao) " +
                     "BETWEEN strftime('%m-%d', 'now', 'localtime') " +
                     "AND strftime('%m-%d', 'now', '+30 days', 'localtime') " +
                     "ORDER BY strftime('%m-%d', data_admissao)";
        List<String> aniversariantes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                String data = rs.getString("data_admissao");
                // Formata a data para dd/MM
                String diaMes = data.substring(8, 10) + "/" + data.substring(5, 7);
                aniversariantes.add(diaMes + " - " + nome);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return aniversariantes;
    }

    public java.util.Map<String, Integer> getContagemPorDepartamento() {
        String sql = "SELECT d.nome, COUNT(c.id) as total " +
                     "FROM colaboradores c " +
                     "JOIN departamentos d ON c.id_departamento = d.id " +
                     "GROUP BY d.nome";
        java.util.Map<String, Integer> contagem = new java.util.LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contagem.put(rs.getString("nome"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contagem;
    }
}