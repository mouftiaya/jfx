package com.stockSystem.controller;

import com.stockSystem.dao.TransactionDAO;
import com.stockSystem.dao.TransactionDAOImpl;
import com.stockSystem.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.control.Alert;

public class TransactionsController {

    @FXML private TextField searchField;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> typeCol;
    @FXML private TableColumn<Transaction, String> statusCol;
    @FXML private TableColumn<Transaction, String> totalPriceCol;
    @FXML private TableColumn<Transaction, Integer> totalProductsCol;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, Void> actionsCol;

    private TransactionDAO transactionDAO;
    private ObservableList<Transaction> allTransactions;

    public void initialize() {
        transactionDAO = new TransactionDAOImpl();
        
        // Setup columns
        typeCol.setCellValueFactory(cellData -> {
            Transaction.Type type = cellData.getValue().getType();
            return new javafx.beans.property.SimpleStringProperty(
                type == Transaction.Type.IN ? "PURCHASE" : "SALE"
            );
        });
        
        statusCol.setCellValueFactory(cellData -> {
            Transaction.Status status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(
                status != null ? status.name() : "COMPLETED"
            );
        });
        
        // Make status column editable with ComboBox
        statusCol.setCellFactory(column -> new TableCell<Transaction, String>() {
            private final ComboBox<Transaction.Status> comboBox = new ComboBox<>();
            
            {
                comboBox.getItems().addAll(Transaction.Status.values());
                comboBox.setPrefWidth(150);
                comboBox.setStyle("-fx-background-color: white;");
                
                comboBox.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if (transaction != null && comboBox.getValue() != null) {
                        transaction.setStatus(comboBox.getValue());
                        try {
                            transactionDAO.update(transaction);
                            // Update the observable list to reflect changes
                            int index = allTransactions.indexOf(transaction);
                            if (index >= 0) {
                                allTransactions.set(index, transaction);
                            }
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update status: " + e.getMessage());
                        }
                    }
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if (transaction != null) {
                        Transaction.Status currentStatus = transaction.getStatus();
                        if (currentStatus == null) {
                            currentStatus = Transaction.Status.COMPLETED;
                            transaction.setStatus(currentStatus);
                            transactionDAO.update(transaction);
                        }
                        comboBox.setValue(currentStatus);
                        setGraphic(comboBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        totalPriceCol.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            double total = t.getQuantity() * 
                (t.getRelatedProduct().getSellPrice() != null ? 
                    t.getRelatedProduct().getSellPrice().doubleValue() : 0.0);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", total));
        });
        
        totalProductsCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        dateCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
            String formatted = cellData.getValue().getDate().format(formatter);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        
        // Actions column with View Details button
        actionsCol.setCellFactory(param -> new TableCell<Transaction, Void>() {
            private final Button btn = new Button("View Details");
            
            {
                btn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    showTransactionDetails(transaction);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        // Load data
        loadData();
        
        // Setup search
        setupSearch();
    }

    private void loadData() {
        List<Transaction> transactions = transactionDAO.findAll();
        allTransactions = FXCollections.observableArrayList(transactions);
    }

    private void setupSearch() {
        FilteredList<Transaction> filteredData = new FilteredList<>(allTransactions, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(transaction -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (transaction.getType().name().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (transaction.getRelatedProduct() != null && 
                    transaction.getRelatedProduct().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });
        
        SortedList<Transaction> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transactionTable.comparatorProperty());
        transactionTable.setItems(sortedData);
    }

    @FXML
    private void handleSearch() {
        // Search is handled automatically via the text property listener
    }

    private void showTransactionDetails(Transaction transaction) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Details");
        alert.setHeaderText("Transaction ID: " + transaction.getId());
        alert.setContentText(
            "Type: " + (transaction.getType() == Transaction.Type.IN ? "PURCHASE" : "SALE") + "\n" +
            "Status: " + (transaction.getStatus() != null ? transaction.getStatus().name() : "COMPLETED") + "\n" +
            "Product: " + (transaction.getRelatedProduct() != null ? transaction.getRelatedProduct().getName() : "N/A") + "\n" +
            "Quantity: " + transaction.getQuantity() + "\n" +
            "Date: " + transaction.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a"))
        );
        alert.showAndWait();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
