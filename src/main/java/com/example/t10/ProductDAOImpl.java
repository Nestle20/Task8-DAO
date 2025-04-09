package com.example.t10;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {
    private final List<Product> products;
    private int nextId;

    public ProductDAOImpl(TagDAO tagDAO) {
        // Используем TagDAO
        this.products = new ArrayList<>();
        this.nextId = 1;

        // Используем ProductList для начального заполнения данных
        ProductList productList = new ProductList(5, tagDAO); // Создаем 5 тестовых продуктов
        this.products.addAll(productList.getProducts());

        // Устанавливаем nextId на основе последнего ID в списке
        if (!products.isEmpty()) {
            this.nextId = products.get(products.size() - 1).getId() + 1;
        }
    }

    @Override
    public void addProduct(Product product) {
        product.setId(nextId++); // Устанавливаем ID и увеличиваем счетчик
        products.add(product);
    }

    @Override
    public void updateProduct(Product product) {
        Optional<Product> existingProduct = products.stream()
                .filter(p -> p.getId() == product.getId())
                .findFirst();
        existingProduct.ifPresent(p -> {
            p.setName(product.getName());
            p.setQuantity(product.getQuantity());
            p.setTag(product.getTag());
        });
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Возвращаем копию списка
    }
}