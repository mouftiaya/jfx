package com.stockSystem.controller;

import com.stockSystem.model.Supplier;
import com.stockSystem.service.SupplierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class SupplierController {
    @FXML private TextField searchField;
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> nameCol;

    private final SupplierService supplierService;
    private ObservableList<Supplier> allSuppliers;

    public SupplierController() {
        this.supplierService = new SupplierService();
    }

    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Add action buttons column
        TableColumn<Supplier, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<Supplier, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #696969; -fx-text-fill: white;");
                
                editBtn.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    handleEditSupplier(supplier);
                });
                
                deleteBtn.setOnAction(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    handleDeleteSupplier(supplier);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
        
        supplierTable.getColumns().add(actionsCol);
        
        loadData();
        setupSearch();
    }

    private void loadData() {
        allSuppliers = FXCollections.observableArrayList(supplierService.findAll());
        supplierTable.setItems(allSuppliers);
    }

    private void setupSearch() {
        FilteredList<Supplier> filteredData = new FilteredList<>(allSuppliers, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(supplier -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return supplier.getName().toLowerCase().contains(lowerCaseFilter) ||
                       (supplier.getContactInfo() != null && 
                        supplier.getContactInfo().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        SortedList<Supplier> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(supplierTable.comparatorProperty());
        supplierTable.setItems(sortedData);
    }

    @FXML
    private void handleAddSupplier() {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Add Supplier");
        dialog.setHeaderText("Enter Supplier Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Supplier Name");
        TextField contact = new TextField();
        contact.setPromptText("Contact Info");
        TextField address = new TextField();
        address.setPromptText("Address");

        grid.add(new Label("Supplier Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Contact Info:"), 0, 1);
        grid.add(contact, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(address, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Supplier supplier = new Supplier(name.getText(), contact.getText());
                supplier.setAddress(address.getText());
                return supplier;
            }
            return null;
        });

        Optional<Supplier> result = dialog.showAndWait();
        result.ifPresent(supplier -> {
            supplierService.save(supplier);
            loadData();
        });
    }

    @FXML
    private void handleEditSupplier(Supplier supplier) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle("Edit Supplier");
        dialog.setHeaderText("Edit Supplier Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(supplier.getName());
        TextField contact = new TextField(supplier.getContactInfo());
        TextField address = new TextField(supplier.getAddress() != null ? supplier.getAddress() : "");

        grid.add(new Label("Supplier Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Contact Info:"), 0, 1);
        grid.add(contact, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(address, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                supplier.setName(name.getText());
                supplier.setContactInfo(contact.getText());
                supplier.setAddress(address.getText());
                return supplier;
            }
            return null;
        });

        Optional<Supplier> result = dialog.showAndWait();
        result.ifPresent(sup -> {
            supplierService.save(sup);
            loadData();
        });
    }

    @FXML
    private void handleDeleteSupplier(Supplier supplier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Supplier");
        alert.setHeaderText("Are you sure you want to delete " + supplier.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            supplierService.delete(supplier);
            loadData();
        }
    }
}
