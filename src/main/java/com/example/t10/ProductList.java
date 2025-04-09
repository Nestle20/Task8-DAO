package com.example.t10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductList {
    private final List<Product> products;

    public ProductList(int size, TagDAO tagDAO) {
        products = new ArrayList<>();
        Random random = new Random();
        List<Tag> tags = tagDAO.getAllTags();

        for (int i = 0; i < size; i++) {
            Tag randomTag = tags.get(random.nextInt(tags.size()));
            Product product = new Product(
                    i + 1,
                    "Product " + (i + 1),
                    random.nextInt(100),
                    randomTag
            );
            products.add(product);
        }
    }

    public List<Product> getProducts() {
        return products;
    }
}