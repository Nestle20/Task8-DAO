package com.example.t10;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductCSVDAOIntegrationTest {
    private ProductCSVDAO dao;
    private TagDAO tagDAO;
    private final String testFilePath = "test_products.csv";

    @BeforeAll
    void setup() throws Exception {
        tagDAO = new TagDAO.Impl();
        // Удаляем старый тестовый файл, если существует
        Files.deleteIfExists(Path.of(testFilePath));
    }

    @BeforeEach
    void init() throws Exception {
        // Создаем новый DAO перед каждым тестом
        dao = new ProductCSVDAO(tagDAO) {
            @Override
            public String getCurrentFilePath() {
                return testFilePath;
            }
        };
        // Очищаем файл перед каждым тестом
        Files.deleteIfExists(Path.of(testFilePath));
    }

    @Test
    void testAddAndGetAllProducts() throws Exception {
        // Проверяем, что изначально список пуст
        assertEquals(0, dao.getAllProducts().size(), "Initially should be empty");

        // Добавляем тестовый продукт
        Tag testTag = tagDAO.getAllTags().get(0);
        Product testProduct = new Product(1, "Test Product", 10, testTag);
        dao.addProduct(testProduct);

        // Проверяем результат
        List<Product> products = dao.getAllProducts();
        assertEquals(1, products.size(), "Should contain one product after addition");
        assertEquals("Test Product", products.get(0).getName());
        assertEquals(10, products.get(0).getQuantity());
    }

    @Test
    void testImportExport() throws Exception {
        // Подготовка тестовых данных
        Tag testTag = tagDAO.getAllTags().get(0);
        Product testProduct = new Product(2, "Test Import", 20, testTag);

        // Добавляем и экспортируем
        dao.addProduct(testProduct);
        dao.exportToCSV(testFilePath);

        // Создаем новый DAO для импорта
        ProductCSVDAO importDao = new ProductCSVDAO(tagDAO) {
            @Override
            public String getCurrentFilePath() {
                return testFilePath;
            }
        };
        importDao.importFromCSV(testFilePath);

        // Проверяем импортированные данные
        List<Product> imported = importDao.getAllProducts();
        assertEquals(1, imported.size(), "Should import exactly one product");
        assertEquals("Test Import", imported.get(0).getName());
        assertEquals(20, imported.get(0).getQuantity());
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Добавляем тестовый продукт
        Tag testTag = tagDAO.getAllTags().get(0);
        Product testProduct = new Product(3, "To Update", 30, testTag);
        dao.addProduct(testProduct);

        // Обновляем продукт
        Product toUpdate = dao.getAllProducts().get(0);
        toUpdate.setName("Updated");
        toUpdate.setQuantity(40);
        dao.updateProduct(toUpdate);

        // Проверяем обновление
        List<Product> updated = dao.getAllProducts();
        assertEquals(1, updated.size());
        assertEquals("Updated", updated.get(0).getName());
        assertEquals(40, updated.get(0).getQuantity());
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Добавляем тестовый продукт
        Tag testTag = tagDAO.getAllTags().get(0);
        Product testProduct = new Product(4, "To Delete", 50, testTag);
        dao.addProduct(testProduct);

        // Удаляем продукт
        int idToDelete = dao.getAllProducts().get(0).getId();
        dao.deleteProduct(idToDelete);

        // Проверяем удаление
        assertEquals(0, dao.getAllProducts().size(), "Should be empty after deletion");
    }

    @AfterAll
    void cleanup() throws Exception {
        // Удаляем тестовый файл после всех тестов
        Files.deleteIfExists(Path.of(testFilePath));
    }
}