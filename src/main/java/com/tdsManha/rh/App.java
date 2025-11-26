package com.tdsManha.rh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import com.tdsManha.rh.controller.LoginController;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega a tela de Login
        URL fxmlLocation = getClass().getResource("view/fxml/LoginView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // 👉 CARREGANDO O CSS
        scene.getStylesheets().add(
        getClass().getResource("/App.css").toExternalForm()
        );  

        // Passa o Stage para o controller
        LoginController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
