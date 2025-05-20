package com.example.t10;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductCSVDAO implements ProductDAO {
    private List<Product> products = new ArrayList<>();
    private final TagDAO tagDAO;
    private String currentFilePath;
    private AtomicInteger idGenerator = new AtomicInteger(1);

    public ProductCSVDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        // Попытка загрузить данные из файла по умолчанию
        File defaultFile = new File("products.csv");
        if (defaultFile.exists()) {
            try {
                importFromCSV(defaultFile.getAbsolutePath());
                // Установка idGenerator на максимальный ID + 1
                idGenerator.set(products.stream()
                        .mapToInt(Product::getId)
                        .max()
                        .orElse(0) + 1);
            } catch (Exception e) {
                System.err.println("Ошибка загрузки данных по умолчанию: " + e.getMessage());
                initializeSampleData();
            }
        } else {
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        List<Tag> tags = tagDAO.getAllTags();
        addProduct(new Product(idGenerator.getAndIncrement(), "Наушники", 10, tags.get(0)));
        addProduct(new Product(idGenerator.getAndIncrement(), "Клавиатура", 5, tags.get(0)));
        addProduct(new Product(idGenerator.getAndIncrement(), "Футболка", 20, tags.get(1)));
        saveToCurrentFile();
    }

    @Override
    public void addProduct(Product product) {
        if (product.getId() == 0) {
            product.setId(idGenerator.getAndIncrement());
        }
        products.add(product);
        saveToCurrentFile();
    }

    @Override
    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                break;
            }
        }
        saveToCurrentFile();
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        saveToCurrentFile();
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
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    int quantity = Integer.parseInt(values[2].trim());
                    int tagId = Integer.parseInt(values[3].trim());

                    Tag tag = tagDAO.getAllTags().stream()
                            .filter(t -> t.getId() == tagId)
                            .findFirst()
                            .orElse(tagDAO.getAllTags().get(0));

                    Product product = new Product(id, name, quantity, tag);
                    products.add(product);
                    if (id >= idGenerator.get()) {
                        idGenerator.set(id + 1);
                    }
                }
            }
            currentFilePath = filePath;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения CSV файла", e);
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
            throw new RuntimeException("Ошибка записи в CSV файл", e);
        }
    }

    @Override
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    private void saveToCurrentFile() {
        if (currentFilePath != null) {
            exportToCSV(currentFilePath);
        }
    }

    @Override
    public void switchToDatabaseSource() {
        throw new UnsupportedOperationException("Переключение на базу данных не поддерживается");
    }

    @Override
    public void switchToFileSource() {
        // Уже используем файловый источник
    }

    @Override
    public void switchToInMemorySource() {
        throw new UnsupportedOperationException("Переключение на in-memory не поддерживается");
    }
}