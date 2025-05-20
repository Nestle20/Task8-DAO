package com.example.t10;

import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDBConnectDAO implements ProductDAO {
    private final TagDAO tagDAO;
    private final DBConnect dbConnect;

    public ProductDBConnectDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        this.dbConnect = new DBConnect();
        try {
            dbConnect.connect(false); // false для файловой БД
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            String sql = "SELECT id, name, quantity, tag_id FROM products";

            try (CachedRowSet crs = dbConnect.executeQuery(sql)) {
                while (crs.next()) {
                    int tagId = crs.getInt("tag_id");
                    Tag tag = findTagById(tagId);

                    if (tag != null) {
                        productList.add(new Product(
                                crs.getInt("id"),
                                crs.getString("name"),
                                crs.getInt("quantity"),
                                tag
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении продуктов", e);
        }
        return productList;
    }

    private Tag findTagById(int tagId) {
        return tagDAO.getAllTags().stream()
                .filter(t -> t.getId() == tagId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (name, quantity, tag_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = dbConnect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setInt(3, product.getTag().getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении продукта", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, quantity = ?, tag_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbConnect.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setInt(3, product.getTag().getId());
            pstmt.setInt(4, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении продукта", e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = dbConnect.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении продукта", e);
        }
    }

    @Override
    public void importFromCSV(String filePath) {
        throw new UnsupportedOperationException("Импорт из CSV не поддерживается для базы данных");
    }

    @Override
    public void exportToCSV(String filePath) {
        throw new UnsupportedOperationException("Экспорт в CSV не поддерживается для базы данных");
    }

    @Override
    public String getCurrentFilePath() {
        return null; // Для базы данных файл не используется
    }

    @Override
    public void switchToDatabaseSource() {
        // Уже используем базу данных
    }

    @Override
    public void switchToFileSource() {
        throw new UnsupportedOperationException("Переключение на файловый источник не поддерживается");
    }

    @Override
    public void switchToInMemorySource() {
        throw new UnsupportedOperationException("Переключение на In-Memory не поддерживается");
    }
}