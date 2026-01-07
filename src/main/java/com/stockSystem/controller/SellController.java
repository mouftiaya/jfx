package com.stockSystem.controller;

import com.stockSystem.model.Product;
import com.stockSystem.model.Transaction;
import com.stockSystem.service.StockService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class SellController {

    @FXML private ComboBox<Product> productCombo;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionField;
    @FXML private TextArea noteField;
    @FXML private Button sellButton;

    private StockService stockService;

    public void initialize() {
        stockService = new StockService();
        
        // Load products
        List<Product> products = stockService.getAllProducts();
        productCombo.setItems(FXCollections.observableArrayList(products));
        productCombo.setPromptText("Select a product");
        
        // Set StringConverter to display product name
        productCombo.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return products.stream()
                    .filter(p -> p.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
    }

    @FXML
    private void handleSell() {
        Product selectedProduct = productCombo.getValue();
        
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to sell.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            if (quantity <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be greater than 0.");
                return;
            }

            // Record sell transaction (OUT)
            stockService.recordTransaction(selectedProduct, Transaction.Type.OUT, quantity);

            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Successfully sold " + quantity + " units of " + selectedProduct.getName() + ".");
            
            // Clear form
            productCombo.setValue(null);
            quantityField.clear();
            descriptionField.clear();
            noteField.clear();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid quantity (number).");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Stock", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process sale: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
