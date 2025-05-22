package com.example.t10;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductDBConnectDAOIntegrationTest {
    private ProductDBConnectDAO dao;
    private DBConnect dbConnect;
    private TagDAO tagDAO = new TagDAO.Impl();

    @BeforeAll
    void setup() throws SQLException {
        dbConnect = new DBConnect();
        dbConnect.connect(false);
        dao = new ProductDBConnectDAO(tagDAO);

        // Подготовка тестовых данных
        try (var stmt = dbConnect.getConnection().createStatement()) {
            stmt.execute("DELETE FROM products WHERE name LIKE 'Test%'");
            stmt.execute("INSERT INTO tags (id, name) VALUES (999, 'Test Category')");
        }
    }

    @Test
    void testAddProduct() throws SQLException {
        Tag testTag = new Tag(999, "Test Category");
        int initialCount = dao.getAllProducts().size();

        Product testProduct = new Product(0, "Test Product", 5, testTag);
        dao.addProduct(testProduct);

        List<Product> products = dao.getAllProducts();
        assertEquals(initialCount + 1, products.size());
        assertEquals("Test Product", products.get(products.size()-1).getName());
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = dao.getAllProducts();
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    void testUpdateProduct() {
        Tag testTag = new Tag(999, "Test Category");
        Product testProduct = new Product(0, "Test Update", 10, testTag);
        dao.addProduct(testProduct);

        Product toUpdate = dao.getAllProducts().stream()
                .filter(p -> p.getName().equals("Test Update"))
                .findFirst()
                .orElseThrow();

        toUpdate.setQuantity(20);
        dao.updateProduct(toUpdate);

        Product updated = dao.getAllProducts().stream()
                .filter(p -> p.getId() == toUpdate.getId())
                .findFirst()
                .orElseThrow();

        assertEquals(20, updated.getQuantity());
    }

    @Test
    void testDeleteProduct() {
        Tag testTag = new Tag(999, "Test Category");
        Product testProduct = new Product(0, "Test Delete", 15, testTag);
        dao.addProduct(testProduct);

        int initialSize = dao.getAllProducts().size();
        Product toDelete = dao.getAllProducts().stream()
                .filter(p -> p.getName().equals("Test Delete"))
                .findFirst()
                .orElseThrow();

        dao.deleteProduct(toDelete.getId());
        assertEquals(initialSize - 1, dao.getAllProducts().size());
    }

    @AfterAll
    void cleanup() throws SQLException {
        try (var stmt = dbConnect.getConnection().createStatement()) {
            stmt.execute("DELETE FROM products WHERE name LIKE 'Test%'");
            stmt.execute("DELETE FROM tags WHERE id = 999");
        }
        dbConnect.disconnect();
    }
}