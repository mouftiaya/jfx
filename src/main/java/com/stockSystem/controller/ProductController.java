package com.stockSystem.controller;

import com.stockSystem.model.Product;
import com.stockSystem.model.Transaction;
import com.stockSystem.service.StockService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Long> idCol;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, String> skuCol;
    @FXML private TableColumn<Product, BigDecimal> priceCol;
    @FXML private TableColumn<Product, Integer> quantityCol;

    private StockService stockService;

    public void initialize() {
        stockService = new StockService();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadData();
    }

    private void loadData() {
        List<Product> products = stockService.getAllProducts();
        productTable.setItems(FXCollections.observableArrayList(products));
    }

    @FXML
    private void handleAddProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter Product Details");

        ButtonType loginButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField sku = new TextField();
        sku.setPromptText("SKU");
        TextField price = new TextField();
        price.setPromptText("Price");
        TextField quantity = new TextField();
        quantity.setPromptText("Quantity");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(sku, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(price, 1, 2);
        grid.add(new Label("Initial Qty:"), 0, 3);
        grid.add(quantity, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    Product p = new Product();
                    p.setName(name.getText());
                    p.setSku(sku.getText());
                    p.setSellPrice(new BigDecimal(price.getText()));
                    p.setQuantity(Integer.parseInt(quantity.getText()));
                    return p;
                } catch (Exception e) {
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setTitle("Error");
                   alert.setContentText("Invalid input: " + e.getMessage());
                   alert.showAndWait();
                   return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            stockService.addProduct(product);
            loadData();
        });
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Product");
            alert.setHeaderText("Are you sure you want to delete " + selectedProduct.getName() + "?");
            alert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                stockService.removeProduct(selectedProduct);
                loadData();
            }
        } else {
            showNoSelectionAlert();
        }
    }

    @FXML
    private void handleStockIn() {
        handleTransaction(Transaction.Type.IN);
    }

    @FXML
    private void handleStockOut() {
        handleTransaction(Transaction.Type.OUT);
    }

    private void handleTransaction(Transaction.Type type) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Stock " + type);
            dialog.setHeaderText("Enter quantity to " + (type == Transaction.Type.IN ? "add" : "remove"));
            dialog.setContentText("Quantity:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(qty -> {
                try {
                    int quantity = Integer.parseInt(qty);
                    stockService.recordTransaction(selectedProduct, type, quantity);
                    loadData();
                } catch (NumberFormatException e) {
                    showError("Invalid Number");
                } catch (IllegalArgumentException e) {
                    showError(e.getMessage());
                }
            });
        } else {
            showNoSelectionAlert();
        }
    }

    private void showNoSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Product Selected");
        alert.setContentText("Please select a product in the table.");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
