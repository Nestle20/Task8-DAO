package com.example.t10;

import java.util.List;

public interface ProductDAO {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int id);
    List<Product> getAllProducts();
    void importFromCSV(String filePath);
    void exportToCSV(String filePath);
    void switchToDatabaseSource();
    void switchToFileSource();
}