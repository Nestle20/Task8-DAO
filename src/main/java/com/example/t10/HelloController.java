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

        // Инициализация с CSV источником по умолчанию
        productDAO = new ProductCSVDAO(tagDAO);
        refreshData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                quantityField.setText(String.valueOf(newSelection.getQuantity()));
                tagComboBox.getSelectionModel().select(newSelection.getTag());
            }
        });
    }

    private void setupDataSourceComboBox() {
        dataSourceComboBox.getItems().addAll("H2 Database", "CSV File", "In-Memory");
        dataSourceComboBox.getSelectionModel().select("CSV File");
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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(loadFromFileButton.getScene().getWindow());
        if (file != null) {
            try {
                productDAO = new ProductCSVDAO(tagDAO);
                ((ProductCSVDAO) productDAO).importFromCSV(file.getAbsolutePath());
                dataSourceComboBox.getSelectionModel().select("CSV File");
                refreshData();
                saveButton.setDisable(false);
            } catch (Exception e) {
                showAlert("Ошибка загрузки",
                        "Не удалось загрузить данные из файла",
                        e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveToFile() {
        if (productDAO instanceof ProductCSVDAO) {
            ProductCSVDAO csvDao = (ProductCSVDAO) productDAO;
            try {
                csvDao.exportToCSV(csvDao.getCurrentFilePath());
                showAlert("Сохранение", "Успешно", "Данные сохранены в файл");
            } catch (Exception e) {
                showAlert("Ошибка сохранения",
                        "Не удалось сохранить данные",
                        e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty() || selectedTag == null) {
                showAlert("Ошибка ввода",
                        "Не заполнены все поля",
                        "Пожалуйста, заполните все обязательные поля");
                return;
            }

            Product newProduct = new Product(0, name, quantity, selectedTag);
            productDAO.addProduct(newProduct);
            refreshData();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Ошибка ввода",
                    "Некорректное количество",
                    "Введите целое число в поле 'Количество'");
        } catch (Exception e) {
            showAlert("Ошибка добавления",
                    "Не удалось добавить продукт",
                    e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Ошибка выбора",
                    "Продукт не выбран",
                    "Выберите продукт для редактирования");
            return;
        }

        try {
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            Tag selectedTag = tagComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty() || selectedTag == null) {
                showAlert("Ошибка ввода",
                        "Не заполнены все поля",
                        "Пожалуйста, заполните все обязательные поля");
                return;
            }

            selectedProduct.setName(name);
            selectedProduct.setQuantity(quantity);
            selectedProduct.setTag(selectedTag);
            productDAO.updateProduct(selectedProduct);
            refreshData();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Ошибка ввода",
                    "Некорректное количество",
                    "Введите целое число в поле 'Количество'");
        } catch (Exception e) {
            showAlert("Ошибка обновления",
                    "Не удалось обновить продукт",
                    e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Ошибка выбора",
                    "Продукт не выбран",
                    "Выберите продукт для удаления");
            return;
        }

        try {
            productDAO.deleteProduct(selectedProduct.getId());
            refreshData();
            clearFields();
        } catch (Exception e) {
            showAlert("Ошибка удаления",
                    "Не удалось удалить продукт",
                    e.getMessage());
        }
    }

    private void switchDataSource() {
        String selected = dataSourceComboBox.getSelectionModel().getSelectedItem();
        try {
            if ("H2 Database".equals(selected)) {
                productDAO = new ProductDBConnectDAO(tagDAO);
                saveButton.setDisable(true);
            } else if ("CSV File".equals(selected)) {
                productDAO = new ProductCSVDAO(tagDAO);
                saveButton.setDisable(false);
            } else {
                productDAO = new ProductInMemoryDAO(tagDAO);
                saveButton.setDisable(true);
            }
            refreshData();
        } catch (Exception e) {
            showAlert("Ошибка переключения",
                    "Не удалось изменить источник данных",
                    e.getMessage());
        }
    }

    private void refreshData() {
        try {
            List<Product> productList = productDAO.getAllProducts();
            products.setAll(productList);
            productTable.setItems(products);
        } catch (Exception e) {
            showAlert("Ошибка данных",
                    "Не удалось загрузить данные",
                    e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        tagComboBox.getSelectionModel().selectFirst();
        productTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}