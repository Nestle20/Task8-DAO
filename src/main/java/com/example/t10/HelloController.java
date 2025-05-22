package com.example.t10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;

public class HelloController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Tag> tagColumn;

    @FXML private ComboBox<String> dataSourceComboBox;
    @FXML private Button loadFromFileButton;
    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<Tag> tagComboBox;
    @FXML private Button saveButton;

    private ProductDAO productDAO;
    private final TagDAO tagDAO = new TagDAO.Impl();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList(tagDAO.getAllTags());

    @FXML
    public void initialize() {
        setupTableColumns();
        setupDataSourceComboBox();
        setupTagComboBox();

        productDAO = new ProductCSVDAO(tagDAO);
        refreshData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        productTable.setItems(products);
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.getName());
                quantityField.setText(String.valueOf(newVal.getQuantity()));
                tagComboBox.getSelectionModel().select(newVal.getTag());
            }
        });
    }

    private void setupDataSourceComboBox() {
        dataSourceComboBox.getItems().addAll("CSV File", "H2 Database", "In-Memory");
        dataSourceComboBox.getSelectionModel().selectFirst();
        dataSourceComboBox.setOnAction(e -> switchDataSource());
    }

    private void setupTagComboBox() {
        tagComboBox.setItems(tags);
        tagComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLoadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(loadFromFileButton.getScene().getWindow());
        if (file != null) {
            try {
                ((ProductCSVDAO) productDAO).importFromCSV(file.getAbsolutePath());
                refreshData();
                showAlert("Success", "Data loaded", "File loaded: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Load failed", "Error loading file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveToFile() {
        if (productDAO instanceof ProductCSVDAO) {
            try {
                ((ProductCSVDAO) productDAO).exportToCSV(((ProductCSVDAO) productDAO).getCurrentFilePath());
                showAlert("Success", "Data saved", "Data saved to CSV file");
            } catch (Exception e) {
                showAlert("Error", "Save failed", "Error saving file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            Product product = createProductFromFields();
            productDAO.addProduct(product);
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Invalid data", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No selection", "Please select a product to update");
            return;
        }

        try {
            Product product = createProductFromFields();
            product.setId(selected.getId());
            productDAO.updateProduct(product);
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Invalid data", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No selection", "Please select a product to delete");
            return;
        }

        productDAO.deleteProduct(selected.getId());
        refreshData();
        clearFields();
    }

    private Product createProductFromFields() throws Exception {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new Exception("Product name cannot be empty");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Quantity must be a number");
        }

        Tag tag = tagComboBox.getSelectionModel().getSelectedItem();
        if (tag == null) {
            throw new Exception("Please select a tag");
        }

        return new Product(0, name, quantity, tag);
    }

    private void switchDataSource() {
        String selected = dataSourceComboBox.getSelectionModel().getSelectedItem();
        try {
            productDAO = ProductDAOFactory.createProductDAO(selected, tagDAO);
            saveButton.setDisable(!selected.equals("CSV File"));
            refreshData();
        } catch (Exception e) {
            showAlert("Error", "Switch failed", "Error switching data source: " + e.getMessage());
        }
    }

    private void refreshData() {
        products.setAll(productDAO.getAllProducts());
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        tagComboBox.getSelectionModel().selectFirst();
        productTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}