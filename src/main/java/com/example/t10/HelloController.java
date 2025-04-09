package com.example.t10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class HelloController {
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Tag> tagColumn;

    private final ProductDAO productDAO;
    private final TagDAO tagDAO;
    private final ObservableList<Product> products;
    private final ObservableList<Tag> tags;

    public HelloController() {
        this.tagDAO = new TagListImpl(); // Инициализируем TagDAO
        this.productDAO = new ProductDAOImpl(tagDAO); // Передаем TagDAO в ProductDAO
        this.products = FXCollections.observableArrayList();
        this.tags = FXCollections.observableArrayList(tagDAO.getAllTags()); // Загружаем теги
    }

    @FXML
    public void initialize() {
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));

        // Загружаем данные из ProductDAO
        products.addAll(productDAO.getAllProducts());
        productTable.setItems(products);
    }

    @FXML
    private void handleAddProduct() {
        // Создаем диалоговое окно
        Dialog<Pair<String, Pair<Integer, Tag>>> dialog = new Dialog<>();
        dialog.setTitle("Добавить продукт");
        dialog.setHeaderText("Введите данные о новом продукте");

        // Устанавливаем кнопки (ОК и Отмена)
        ButtonType addButtonType = new ButtonType("Добавить", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Создаем поля для ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Название");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Количество");
        ComboBox<Tag> tagComboBox = new ComboBox<>(tags); // Выбор тега из списка

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Количество:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Тег:"), 0, 2);
        grid.add(tagComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Преобразуем результат в объект Pair
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    Tag tag = tagComboBox.getValue();
                    return new Pair<>(name, new Pair<>(quantity, tag));
                } catch (NumberFormatException e) {
                    // Обработка ошибки ввода количества
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Некорректный ввод");
                    alert.setContentText("Количество должно быть числом.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        // Обрабатываем результат
        dialog.showAndWait().ifPresent(result -> {
            String name = result.getKey();
            int quantity = result.getValue().getKey();
            Tag tag = result.getValue().getValue();

            // Создаем новый продукт
            Product newProduct = new Product(0, name, quantity, tag); // ID будет установлен в ProductDAO
            productDAO.addProduct(newProduct); // Добавляем продукт через DAO
            products.add(newProduct); // Обновляем таблицу
            productTable.refresh();
        });
    }

    @FXML
    private void handleUpdateProduct() {
        // Логика обновления продукта
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Создаем диалоговое окно для обновления продукта
            Dialog<Pair<String, Pair<Integer, Tag>>> dialog = new Dialog<>();
            dialog.setTitle("Обновить продукт");
            dialog.setHeaderText("Редактирование продукта: " + selectedProduct.getName());

            // Устанавливаем кнопки (ОК и Отмена)
            ButtonType updateButtonType = new ButtonType("Обновить", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Создаем поля для ввода
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nameField = new TextField(selectedProduct.getName());
            nameField.setPromptText("Название");
            TextField quantityField = new TextField(String.valueOf(selectedProduct.getQuantity()));
            quantityField.setPromptText("Количество");
            ComboBox<Tag> tagComboBox = new ComboBox<>(tags); // Выбор тега из списка
            tagComboBox.setValue(selectedProduct.getTag()); // Устанавливаем текущий тег

            grid.add(new Label("Название:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Количество:"), 0, 1);
            grid.add(quantityField, 1, 1);
            grid.add(new Label("Тег:"), 0, 2);
            grid.add(tagComboBox, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Преобразуем результат в объект Pair
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    try {
                        String name = nameField.getText();
                        int quantity = Integer.parseInt(quantityField.getText());
                        Tag tag = tagComboBox.getValue();
                        return new Pair<>(name, new Pair<>(quantity, tag));
                    } catch (NumberFormatException e) {
                        // Обработка ошибки ввода количества
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Некорректный ввод");
                        alert.setContentText("Количество должно быть числом.");
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            // Обрабатываем результат
            dialog.showAndWait().ifPresent(result -> {
                String name = result.getKey();
                int quantity = result.getValue().getKey();
                Tag tag = result.getValue().getValue();

                // Обновляем выбранный продукт
                selectedProduct.setName(name);
                selectedProduct.setQuantity(quantity);
                selectedProduct.setTag(tag);

                // Обновляем продукт в DAO
                productDAO.updateProduct(selectedProduct);

                // Обновляем таблицу
                productTable.refresh();
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Продукт не выбран");
            alert.setContentText("Пожалуйста, выберите продукт для обновления.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteProduct() {
        // Логика удаления продукта
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productDAO.deleteProduct(selectedProduct.getId()); // Удаляем продукт через DAO
            products.remove(selectedProduct); // Обновляем таблицу
            productTable.refresh();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Продукт не выбран");
            alert.setContentText("Пожалуйста, выберите продукт для удаления.");
            alert.showAndWait();
        }
    }
}