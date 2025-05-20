package com.example.t10;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductInMemoryDAO implements ProductDAO {
    private static final String DATA_FILE = "inmemory_data.ser";
    private List<Product> products;
    private final TagDAO tagDAO;
    private AtomicInteger idGenerator;
    private String currentFilePath;

    public ProductInMemoryDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                products = (List<Product>) ois.readObject();
                idGenerator = new AtomicInteger(products.stream()
                        .mapToInt(Product::getId)
                        .max()
                        .orElse(0) + 1);
                currentFilePath = DATA_FILE;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка загрузки данных: " + e.getMessage());
                initializeSampleData();
            }
        } else {
            initializeSampleData();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(products);
            currentFilePath = DATA_FILE;
        } catch (IOException e) {
            System.err.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }

    private void initializeSampleData() {
        products = new ArrayList<>();
        idGenerator = new AtomicInteger(1);
        List<Tag> tags = tagDAO.getAllTags();
        addProduct(new Product(idGenerator.getAndIncrement(), "Wireless Mouse", 15, tags.get(0)));
        addProduct(new Product(idGenerator.getAndIncrement(), "T-Shirt", 25, tags.get(1)));
        addProduct(new Product(idGenerator.getAndIncrement(), "Coffee Mug", 30, tags.get(2)));
    }

    @Override
    public void addProduct(Product product) {
        product.setId(idGenerator.getAndIncrement());
        products.add(product);
        saveData();
    }

    @Override
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        saveData();
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        saveData();
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public void importFromCSV(String filePath) {
        throw new UnsupportedOperationException("Import from CSV not supported for in-memory storage");
    }

    @Override
    public void exportToCSV(String filePath) {
        throw new UnsupportedOperationException("Export to CSV not supported for in-memory storage");
    }

    @Override
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    @Override
    public void switchToDatabaseSource() {
        throw new UnsupportedOperationException("Switch to database not supported");
    }

    @Override
    public void switchToFileSource() {
        throw new UnsupportedOperationException("Switch to file source not supported");
    }

    @Override
    public void switchToInMemorySource() {
        // Already in memory
    }
}