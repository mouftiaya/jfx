package com.stockSystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardController {

    @FXML private BorderPane mainLayout;
    @FXML private VBox contentArea;

    public void initialize() {
        // Load Dashboard View by default
        showDashboard();
    }

    @FXML
    private void showProducts() {
        loadView("product_view");
    }

    @FXML
    private void showCategories() {
        loadView("category_view");
    }

    @FXML
    private void showSuppliers() {
        loadView("supplier_view");
    }

    @FXML
    private void showUsers() {
        loadView("user_view");
    }

    @FXML
    private void showPurchase() {
        loadView("purchase_view");
    }

    @FXML
    private void showSell() {
        loadView("sell_view");
    }

    @FXML
    private void showDashboard() {
        loadView("dashboard_content");
    }

    @FXML
    private void showTransactions() {
        loadView("transactions_view");
    }

    @FXML
    private void showProfile() {
        loadView("profile_view");
    }

    @FXML
    private void logout() {
        // TODO: Implement logout functionality
        showAlert("Logout functionality not implemented yet");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml + ".fxml"));
            Parent view = loader.load();
            
            // Check if contentArea is part of a BorderPane (it is originally in center)
            // But mainLayout IS the BorderPane.
            // When we use mainLayout.setCenter(view), we replace 'contentArea'.
            // That is fine, as long as we don't need 'contentArea' for subsequent calls.
            // Or we can invoke setCenter on the root.
            
            // Getting the root node if mainLayout is not injected yet (it might be null depending on where fx:id is)
            // dashboard.fxml root is BorderPane.
            // Let's rely on contentArea.getParent() 
             if (contentArea != null && contentArea.getParent() instanceof BorderPane) {
                ((BorderPane) contentArea.getParent()).setCenter(view);
             } else if (mainLayout != null) {
                 mainLayout.setCenter(view);
             }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load view: " + fxml);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
