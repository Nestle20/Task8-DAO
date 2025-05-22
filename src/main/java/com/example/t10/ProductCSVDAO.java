package com.example.t10;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCSVDAO implements ProductDAO {
    private final TagDAO tagDAO;
    private String currentFilePath;
    private final List<Product> products = new ArrayList<>();
    private String delimiter = ","; // По умолчанию используем запятую

    public ProductCSVDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        this.currentFilePath = Config.getCsvFilePath();
        detectDelimiter(); // Определяем разделитель
        initializeCSVFile();
    }

    private void detectDelimiter() {
        try (BufferedReader br = new BufferedReader(new FileReader(currentFilePath))) {
            String firstLine = br.readLine();
            if (firstLine != null && firstLine.contains(";")) {
                delimiter = ";";
            }
        } catch (Exception e) {
            System.err.println("Не удалось определить разделитель: " + e.getMessage());
        }
    }

    private void initializeCSVFile() {
        try {
            File file = new File(currentFilePath);

            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Создан новый CSV файл: " + file.getAbsolutePath());
                return;
            }

            if (file.length() == 0) {
                System.out.println("CSV файл пуст: " + file.getAbsolutePath());
                return;
            }

            importFromCSV(currentFilePath);
            System.out.println("Данные успешно загружены из CSV (" + delimiter + " разделитель): " +
                    products.size() + " записей");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации CSV: " + e.getMessage());
        }
    }

    @Override
    public void importFromCSV(String filePath) throws Exception {
        this.currentFilePath = filePath;
        products.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Пропускаем заголовок, если он есть
                if (isFirstLine && (line.startsWith("id") || line.startsWith("ID"))) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(delimiter);
                if (values.length < 4) {
                    System.err.println("Пропущена строка - неверный формат: " + line);
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
                            .orElseThrow(() -> new Exception("Тег с ID " + tagId + " не найден"));

                    products.add(new Product(id, name, quantity, tag));
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга строки: " + line + " - " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void exportToCSV(String filePath) throws Exception {
        this.currentFilePath = filePath;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Записываем заголовок
            bw.write("id" + delimiter + "name" + delimiter + "quantity" + delimiter + "tag_id");
            bw.newLine();

            for (Product p : products) {
                bw.write(String.join(delimiter,
                        String.valueOf(p.getId()),
                        p.getName(),
                        String.valueOf(p.getQuantity()),
                        String.valueOf(p.getTag().getId())));
                bw.newLine();
            }
        }
    }

    // Остальные методы остаются без изменений
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
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    private void saveToCSV() {
        try {
            exportToCSV(currentFilePath);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения в CSV: " + e.getMessage());
        }
    }
}