package com.example.t10;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductInMemoryDAOIntegrationTest {
    private ProductInMemoryDAO dao;
    private TagDAO tagDAO = new TagDAO.Impl();

    @BeforeEach
    void setup() {
        dao = new ProductInMemoryDAO(tagDAO);
    }

    @Test
    void testAddProduct() {
        Tag testTag = tagDAO.getAllTags().get(0);
        int initialSize = dao.getAllProducts().size();

        Product testProduct = new Product(0, "Test Memory", 45, testTag);
        dao.addProduct(testProduct);

        assertEquals(initialSize + 1, dao.getAllProducts().size());
    }

    @Test
    void testInitialData() {
        List<Product> products = dao.getAllProducts();
        assertFalse(products.isEmpty());
        assertEquals("Wireless Mouse", products.get(0).getName());
    }

    @Test
    void testUpdateProduct() {
        Product toUpdate = dao.getAllProducts().get(0);
        toUpdate.setQuantity(50);
        dao.updateProduct(toUpdate);

        assertEquals(50, dao.getAllProducts().get(0).getQuantity());
    }

    @Test
    void testDeleteProduct() {
        int initialSize = dao.getAllProducts().size();
        Product toDelete = dao.getAllProducts().get(0);

        dao.deleteProduct(toDelete.getId());
        assertEquals(initialSize - 1, dao.getAllProducts().size());
    }
}