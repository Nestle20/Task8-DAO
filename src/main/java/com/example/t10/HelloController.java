package com.example.t10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;

public class HelloController {
    private final String tipsfordelet = "Загрузка файлов";
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


    private ProductDAO productDAO;
    private final TagDAO tagDAO = new TagListImpl();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList(tagDAO.getAllTags());

    @FXML
    public void initialize() {
        Tooltip tooltip = new Tooltip(tipsfordelet);
        loadFromFileButton.setTooltip(tooltip);
        try {
            setupTableColumns();
            setupDataSourceComboBox();
            setupTagComboBox();
            productDAO = new ProductDBConnectDAO(tagDAO);
            refreshData();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
    }

    private void setupDataSourceComboBox() {
        dataSourceComboBox.getItems().addAll("H2 Database", "CSV File");
        dataSourceComboBox.getSelectionModel().select(0);
        dataSourceComboBox.setOnAction(e -> switchDataSource());
    }

    private void setupTagComboBox() {
        tagComboBox.setItems(tags);
        tagComboBox.getSelectionModel().selectFirst();
    }

    private void switchDataSource() {
        String selected = dataSourceComboBox.getSelectionModel().getSelectedItem();
        try {
            if ("H2 Database".equals(selected)) {
                productDAO = new ProductDBConnectDAO(tagDAO);
            } else {
                productDAO = new ProductCSVDAO(tagDAO);
            }
            refreshData();
        } catch (Exception e) {
            showAlert("Error", "Failed to switch data source", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(loadFromFileButton.getScene().getWindow());
        if (file != null) {
            try {
                productDAO = new ProductCSVDAO(tagDAO);
                ((ProductCSVDAO) productDAO).importFromCSV(file.getAbsolutePath());
                dataSourceComboBox.getSelectionModel().select("CSV File");
                refreshData();
            } catch (Exception e) {
                showAlert("Error", "Failed to load file", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty() || selectedTag == null) {
                showAlert("Error", "Validation Error", "Please fill all fields");
                return;
            }

            Product newProduct = new Product(0, name, quantity, selectedTag);
            productDAO.addProduct(newProduct);
            refreshData();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Quantity", "Please enter a valid number for quantity");
        } catch (Exception e) {
            showAlert("Error", "Failed to add product", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "No Selection", "Please select a product to update");
            return;
        }

        try {
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty() || selectedTag == null) {
                showAlert("Error", "Validation Error", "Please fill all fields");
                return;
            }

            selectedProduct.setName(name);
            selectedProduct.setQuantity(quantity);
            selectedProduct.setTag(selectedTag);
            productDAO.updateProduct(selectedProduct);
            refreshData();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Quantity", "Please enter a valid number for quantity");
        } catch (Exception e) {
            showAlert("Error", "Failed to update product", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "No Selection", "Please select a product to delete");
            return;
        }

        try {
            productDAO.deleteProduct(selectedProduct.getId());
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to delete product", e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshData() {
        try {
            products.setAll(productDAO.getAllProducts());
            productTable.setItems(products);
        } catch (Exception e) {
            showAlert("Error", "Failed to load data", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        tagComboBox.getSelectionModel().selectFirst();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}