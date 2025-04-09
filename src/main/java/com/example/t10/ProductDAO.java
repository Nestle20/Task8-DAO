package com.example.t10;

import java.util.List;

public interface ProductDAO {
    // Добавить продукт
    void addProduct(Product product);

    // Обновить продукт
    void updateProduct(Product product);

    // Удалить продукт по ID
    void deleteProduct(int id);

    // Получить все продукты
    List<Product> getAllProducts();
}