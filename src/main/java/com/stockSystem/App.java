package com.stockSystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Bootstrap: Create default admin if no users exist
        new com.stockSystem.service.UserService().createDefaultAdminIfNoUsers(); // Ensure admin exists

        // Load Login Screen
        scene = new Scene(loadFXML("login"), 400, 300);
        stage.setScene(scene);
        stage.setTitle("Stock Management System - Login");
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
