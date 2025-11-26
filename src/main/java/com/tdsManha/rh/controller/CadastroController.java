package com.tdsManha.rh.controller;

import com.tdsManha.rh.model.dao.UsuarioDAO;
import com.tdsManha.rh.model.entity.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroController {

    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private PasswordField confirmaSenhaField;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCadastroButton() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = senhaField.getText().trim();
        String confirmaSenha = confirmaSenhaField.getText().trim();

        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Todos os campos são obrigatórios.");
            return;
        }

        // Validação de Formato de E-mail
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "O formato do e-mail é inválido.");
            return;
        }

        if (senha.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "A senha deve ter no mínimo 8 caracteres.");
            return;
        }

        if (!senha.equals(confirmaSenha)) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "As senhas não coincidem.");
            return;
        }

        if (usuarioDAO.findUserByEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Este e-mail já está cadastrado.");
            return;
        }

        // Se todas as validações passarem
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);

        if (usuarioDAO.addUser(novoUsuario)) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
            stage.close(); // Fecha a janela de cadastro
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Não foi possível cadastrar o usuário.");
        }
    }

    // Verifica se o e-mail segue formato padrão
    private boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(EMAIL_REGEX);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
