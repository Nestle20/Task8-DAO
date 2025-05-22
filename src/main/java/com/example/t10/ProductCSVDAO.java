package com.example.t10;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCSVDAO implements ProductDAO {
    private final TagDAO tagDAO;
    private String currentFilePath;
    private final List<Product> products = new ArrayList<>();

    public ProductCSVDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        this.currentFilePath = Config.getCsvFilePath();
        loadInitialData();
    }

    private void loadInitialData() {
        File file = new File(currentFilePath);
        if (file.exists() && file.length() > 0) {
            try {
                importFromCSV(currentFilePath);
            } catch (Exception e) {
                System.err.println("Error loading CSV: " + e.getMessage());
            }
        }
    }

    @Override
    public void addProduct(Product product) {
        int newId = products.stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0) + 1;
        product.setId(newId);
        products.add(product);
        saveToCSV();
    }

    @Override
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        saveToCSV();
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);
        saveToCSV();
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public void importFromCSV(String filePath) throws Exception {
        this.currentFilePath = filePath;
        products.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] values = line.split(";");
                if (values.length < 4) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    int quantity = Integer.parseInt(values[2].trim());
                    int tagId = Integer.parseInt(values[3].trim());

                    Tag tag = tagDAO.getAllTags().stream()
                            .filter(t -> t.getId() == tagId)
                            .findFirst()
                            .orElseThrow(() -> new Exception("Tag not found: " + tagId));

                    products.add(new Product(id, name, quantity, tag));
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void exportToCSV(String filePath) throws Exception {
        this.currentFilePath = filePath;
        saveToCSV();
    }

    private void saveToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(currentFilePath))) {
            for (Product p : products) {
                bw.write(String.format("%d;%s;%d;%d",
                        p.getId(),
                        p.getName(),
                        p.getQuantity(),
                        p.getTag().getId()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving CSV: " + e.getMessage());
        }
    }

    @Override
    public String getCurrentFilePath() {
        return currentFilePath;
    }
}