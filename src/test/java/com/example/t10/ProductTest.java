package com.example.t10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testProductCreation() {
        Tag testTag = new Tag(1, "Test");
        Product product = new Product(1, "Test Product", 10, testTag);

        assertEquals(1, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(10, product.getQuantity());
        assertEquals(testTag, product.getTag());
    }

    @Test
    void testSetters() {
        Tag tag1 = new Tag(1, "Tag 1");
        Tag tag2 = new Tag(2, "Tag 2");
        Product product = new Product(1, "Original", 5, tag1);

        product.setId(2);
        product.setName("Updated");
        product.setQuantity(15);
        product.setTag(tag2);

        assertEquals(2, product.getId());
        assertEquals("Updated", product.getName());
        assertEquals(15, product.getQuantity());
        assertEquals(tag2, product.getTag());
    }
}