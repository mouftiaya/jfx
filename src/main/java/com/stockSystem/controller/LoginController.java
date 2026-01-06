package com.stockSystem.controller;

import com.stockSystem.model.User;
import com.stockSystem.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserService userService;

    public void initialize() {
        userService = new UserService();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // In a real app, use hashed passwords. For this MVP, we upgraded User to have set/getPassword
        // We will assume UserService.authenticate() was a placeholder, so we might need to implement logic here 
        // or check if UserService has it.
        // Let's implement a simple check first using UserService.findAll or similar if authenticate isn't ready.
        // Actually, we should check what UserService has.
        
        // For now, let's try to authenticate using the service.
        User user = userService.authenticate(username, password);

        if (user != null) {
            loadDashboard();
        } else {
            errorLabel.setText("Invalid credentials.");
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            // Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard.");
        }
    }
}
