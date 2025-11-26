package com.tdsManha.rh.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import com.tdsManha.rh.model.dao.UsuarioDAO;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private Label statusLabel;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Stage stage;

    // Método para receber o Stage da classe App
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLoginButton() {
        String email = emailField.getText().trim();
        String senha = senhaField.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            return;
        }
        
        // Validação de Formato de E-mail
        if (!isValidEmail(email)) {
            statusLabel.setText("O formato do e-mail é inválido.");
            return;
        }

        // Tenta fazer o login usando o DAO
        if (usuarioDAO.checkLogin(email, senha) != null) {
            statusLabel.setText("Login bem-sucedido!");
            openMainWindow();
        } else {
            // Este erro inclui: E-mail não encontrado OU Senha incorreta
            statusLabel.setText("E-mail ou senha inválidos.");
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tdsManha/rh/view/fxml/CadastroView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage registerStage = new Stage();
            registerStage.setTitle("Cadastro de Usuário");
            registerStage.initModality(Modality.WINDOW_MODAL);
            registerStage.initOwner(stage);
            registerStage.setScene(scene);

            CadastroController controller = loader.getController();
            controller.setStage(registerStage);

            registerStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tdsManha/rh/view/fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load(), 1024, 768);
            Stage mainStage = new Stage();
            mainStage.setTitle("Sistema de Gestão de RH");
            mainStage.setScene(scene);
            mainStage.show();

            // Fecha a janela de login
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica se a string fornecida corresponde a um formato de e-mail básico.
     */
    private boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(EMAIL_REGEX);
    }
}