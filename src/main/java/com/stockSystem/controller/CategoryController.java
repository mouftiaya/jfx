package com.stockSystem.controller;

import com.stockSystem.model.Category;
import com.stockSystem.service.CategoryService;
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

public class CategoryController {
    @FXML private TextField categoryNameField;
    @FXML private TextField searchField;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> nameCol;

    private final CategoryService categoryService;
    private ObservableList<Category> allCategories;

    public CategoryController() {
        this.categoryService = new CategoryService();
    }

    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Add action buttons column
        TableColumn<Category, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<Category, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox hbox = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #696969; -fx-text-fill: white;");
                
                editBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    handleEditCategory(category);
                });
                
                deleteBtn.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    handleDeleteCategory(category);
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
        
        categoryTable.getColumns().add(actionsCol);
        
        loadData();
        setupSearch();
    }

    private void loadData() {
        allCategories = FXCollections.observableArrayList(categoryService.findAll());
        categoryTable.setItems(allCategories);
    }

    private void setupSearch() {
        FilteredList<Category> filteredData = new FilteredList<>(allCategories, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(category -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return category.getName().toLowerCase().contains(lowerCaseFilter) ||
                       (category.getDescription() != null && 
                        category.getDescription().toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        SortedList<Category> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(categoryTable.comparatorProperty());
        categoryTable.setItems(sortedData);
    }

    @FXML
    private void handleAddCategory() {
        String name = categoryNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a category name.");
            return;
        }
        
        Category category = new Category(name, "");
        categoryService.save(category);
        categoryNameField.clear();
        loadData();
    }

    @FXML
    private void handleEditCategory(Category category) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit Category Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(category.getName());
        TextField description = new TextField(category.getDescription() != null ? category.getDescription() : "");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                category.setName(name.getText());
                category.setDescription(description.getText());
                return category;
            }
            return null;
        });

        Optional<Category> result = dialog.showAndWait();
        result.ifPresent(cat -> {
            categoryService.save(cat);
            loadData();
        });
    }

    @FXML
    private void handleDeleteCategory(Category category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Are you sure you want to delete " + category.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            categoryService.delete(category);
            loadData();
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
