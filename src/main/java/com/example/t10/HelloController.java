package com.example.t10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

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
    private final TagDAO tagDAO = new TagListImpl();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList(tagDAO.getAllTags());

    @FXML
    public void initialize() {
        setupTableColumns();
        setupDataSourceComboBox();
        setupTagComboBox();

        // Инициализация с CSV источником
        productDAO = new ProductCSVDAO(tagDAO);
        refreshData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        productTable.setItems(products);

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateFormFields(newSelection);
            }
        });
    }

    private void updateFormFields(Product product) {
        nameField.setText(product.getName());
        quantityField.setText(String.valueOf(product.getQuantity()));
        tagComboBox.getSelectionModel().select(product.getTag());
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
        fileChooser.setTitle("Выберите CSV файл");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(loadFromFileButton.getScene().getWindow());
        if (file != null) {
            try {
                productDAO = new ProductCSVDAO(tagDAO);
                ((ProductCSVDAO) productDAO).importFromCSV(file.getAbsolutePath());
                refreshData();
                showAlert("Успех", "Данные загружены",
                        "Файл успешно загружен: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка загрузки",
                        "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveToFile() {
        if (productDAO instanceof ProductCSVDAO) {
            try {
                ((ProductCSVDAO) productDAO).exportToCSV(
                        ((ProductCSVDAO) productDAO).getCurrentFilePath());
                showAlert("Успех", "Сохранение",
                        "Данные успешно сохранены в CSV файл");
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка сохранения",
                        "Не удалось сохранить данные: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            Product newProduct = createProductFromFields();
            productDAO.addProduct(newProduct);
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Ошибка", "Некорректные данные", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Не выбран продукт",
                    "Пожалуйста, выберите продукт для обновления");
            return;
        }

        try {
            Product updatedProduct = createProductFromFields();
            updatedProduct.setId(selected.getId());
            productDAO.updateProduct(updatedProduct);
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Ошибка", "Некорректные данные", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Не выбран продукт",
                    "Пожалуйста, выберите продукт для удаления");
            return;
        }

        productDAO.deleteProduct(selected.getId());
        refreshData();
        clearFields();
    }

    private Product createProductFromFields() throws Exception {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new Exception("Название продукта не может быть пустым");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Количество должно быть числом");
        }

        Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();
        if (selectedTag == null) {
            throw new Exception("Не выбран тег");
        }

        return new Product(0, name, quantity, selectedTag);
    }

    private void switchDataSource() {
        String selected = dataSourceComboBox.getSelectionModel().getSelectedItem();
        try {
            productDAO = ProductDAOFactory.createProductDAO(selected, tagDAO);
            saveButton.setDisable(!selected.equals("CSV File"));
            refreshData();
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка переключения",
                    "Не удалось изменить источник данных: " + e.getMessage());
        }
    }

    private void refreshData() {
        try {
            products.setAll(productDAO.getAllProducts());
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка данных",
                    "Не удалось загрузить данные: " + e.getMessage());
        }
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