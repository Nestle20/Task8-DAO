package com.example.t10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    @Test
    void testTagCreation() {
        Tag tag = new Tag(1, "Test Tag");

        assertEquals(1, tag.getId());
        assertEquals("Test Tag", tag.getName());
    }

    @Test
    void testToString() {
        Tag tag = new Tag(1, "Test");
        assertEquals("Test", tag.toString());
    }
}