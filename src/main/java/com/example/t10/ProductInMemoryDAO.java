package com.example.t10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductInMemoryDAO implements ProductDAO {
    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final TagDAO tagDAO;
    private String currentFilePath;

    public ProductInMemoryDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        initializeSampleData();
    }

    private void initializeSampleData() {
        List<Tag> tags = tagDAO.getAllTags();
        products.add(new Product(idGenerator.getAndIncrement(), "Wireless Mouse", 15, tags.get(0)));
        products.add(new Product(idGenerator.getAndIncrement(), "T-Shirt", 25, tags.get(1)));
        products.add(new Product(idGenerator.getAndIncrement(), "Coffee Mug", 30, tags.get(2)));
    }

    @Override
    public void addProduct(Product product) {
        product.setId(idGenerator.getAndIncrement());
        products.add(product);
    }

    @Override
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public void importFromCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Import from CSV not supported for in-memory storage");
    }

    @Override
    public void exportToCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Export to CSV not supported for in-memory storage");
    }

    @Override
    public String getCurrentFilePath() {
        return currentFilePath;
    }
}