package com.stockSystem.controller;

import com.stockSystem.model.Category;
import com.stockSystem.model.Product;
import com.stockSystem.model.Supplier;
import com.stockSystem.model.Transaction;
import com.stockSystem.service.CategoryService;
import com.stockSystem.service.StockService;
import com.stockSystem.service.SupplierService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

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
    @FXML private TableColumn<Product, String> categoryCol;
    @FXML private TableColumn<Product, String> supplierCol;

    private StockService stockService;
    private CategoryService categoryService;
    private SupplierService supplierService;

    public void initialize() {
        stockService = new StockService();
        categoryService = new CategoryService();
        supplierService = new SupplierService();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        categoryCol.setCellValueFactory(cellData -> {
            Category category = cellData.getValue().getCategory();
            return new javafx.beans.property.SimpleStringProperty(category != null ? category.getName() : "No Category");
        });
        supplierCol.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue().getSupplier();
            return new javafx.beans.property.SimpleStringProperty(supplier != null ? supplier.getName() : "No Supplier");
        });

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
        
        ComboBox<Category> categoryCombo = new ComboBox<>();
        List<Category> categories = categoryService.findAll();
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
        categoryCombo.setPromptText("Select Category");
        
        // Set StringConverter to display category name
        categoryCombo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? null : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return categories.stream()
                    .filter(cat -> cat.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });

        ComboBox<Supplier> supplierCombo = new ComboBox<>();
        List<Supplier> suppliers = supplierService.findAll();
        supplierCombo.setItems(FXCollections.observableArrayList(suppliers));
        supplierCombo.setPromptText("Select Supplier");
        
        // Set StringConverter to display supplier name
        supplierCombo.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier == null ? null : supplier.getName();
            }

            @Override
            public Supplier fromString(String string) {
                return suppliers.stream()
                    .filter(sup -> sup.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(sku, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(price, 1, 2);
        grid.add(new Label("Initial Qty:"), 0, 3);
        grid.add(quantity, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);
        grid.add(new Label("Supplier:"), 0, 5);
        grid.add(supplierCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    Product p = new Product();
                    p.setName(name.getText());
                    p.setSku(sku.getText());
                    p.setSellPrice(new BigDecimal(price.getText()));
                    p.setQuantity(Integer.parseInt(quantity.getText()));
                    p.setCategory(categoryCombo.getValue()); // Set selected category
                    p.setSupplier(supplierCombo.getValue()); // Set selected supplier
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
                try {
                    stockService.removeProduct(selectedProduct);
                    loadData();
                    showSuccessAlert("Product deleted successfully!");
                } catch (Exception e) {
                    showError("Failed to delete product: " + e.getMessage());
                    e.printStackTrace();
                }
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
