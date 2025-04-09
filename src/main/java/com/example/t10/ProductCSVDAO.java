package com.example.t10;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCSVDAO implements ProductDAO {
    private final List<Product> products = new ArrayList<>();
    private final TagDAO tagDAO;
    private int nextId = 1;
    private String currentFilePath;

    public ProductCSVDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        initializeSampleData();
    }

    private void initializeSampleData() {
        File file = new File("products.csv");
        if (!file.exists()) {
            List<Product> samples = List.of(
                    new Product(1, "Наушники", 5, tagDAO.getAllTags().get(0)),
                    new Product(2, "Клавиатура", 8, tagDAO.getAllTags().get(0)),
                    new Product(3, "Джинсы", 12, tagDAO.getAllTags().get(1))
            );
            products.addAll(samples);
            nextId = samples.size() + 1;
            exportToCSV("products.csv");
        } else {
            importFromCSV("products.csv");
        }
    }

    @Override
    public void addProduct(Product product) {
        product.setId(nextId++);
        products.add(product);
        if (currentFilePath != null) exportToCSV(currentFilePath);
    }

    @Override
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        if (currentFilePath != null) exportToCSV(currentFilePath);
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        if (currentFilePath != null) exportToCSV(currentFilePath);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public void importFromCSV(String filePath) {
        products.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(";");
                if (values.length >= 4) {
                    int id = Integer.parseInt(values[0]);
                    String name = values[1];
                    int quantity = Integer.parseInt(values[2]);
                    int tagId = Integer.parseInt(values[3]);

                    Tag tag = tagDAO.getAllTags().stream()
                            .filter(t -> t.getId() == tagId)
                            .findFirst()
                            .orElse(null);

                    Product product = new Product(id, name, quantity, tag);
                    products.add(product);
                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                }
            }
            currentFilePath = filePath;
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    @Override
    public void exportToCSV(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("id;name;quantity;tag_id");
            for (Product product : products) {
                pw.println(String.format("%d;%s;%d;%d",
                        product.getId(),
                        product.getName(),
                        product.getQuantity(),
                        product.getTag().getId()));
            }
            currentFilePath = filePath;
        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV file", e);
        }
    }

    @Override
    public void switchToDatabaseSource() {
        // Not applicable
    }

    @Override
    public void switchToFileSource() {
        // Already using file source
    }
}