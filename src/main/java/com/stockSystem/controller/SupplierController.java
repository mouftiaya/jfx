package com.stockSystem.controller;

import com.stockSystem.model.Supplier;
import com.stockSystem.service.SupplierService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class SupplierController {
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Long> idCol;
    @FXML private TableColumn<Supplier, String> nameCol;
    @FXML private TableColumn<Supplier, String> contactCol;

    private final SupplierService supplierService;

    public SupplierController() {
        this.supplierService = new SupplierService();
    }

    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        loadData();
    }

    private void loadData() {
        supplierTable.setItems(FXCollections.observableArrayList(supplierService.findAll()));
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
        name.setPromptText("Name");
        TextField contact = new TextField();
        contact.setPromptText("Contact Info");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Contact:"), 0, 1);
        grid.add(contact, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Supplier(name.getText(), contact.getText());
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
    private void handleDeleteSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            supplierService.delete(selected);
            loadData();
        }
    }
}
