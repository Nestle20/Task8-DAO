package com.example.t10;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDBConnectDAO implements ProductDAO {
    private final TagDAO tagDAO;
    private final DBConnect dbConnect;

    public ProductDBConnectDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
        this.dbConnect = new DBConnect();
        try {
            dbConnect.connect();
            initializeDatabase();
            initializeSampleData();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private void initializeDatabase() throws SQLException {
        System.out.println("Initializing database tables...");

        // Создаем таблицу tags
        dbConnect.executeUpdate("""
        CREATE TABLE IF NOT EXISTS tags (
            id INT PRIMARY KEY,
            name VARCHAR(255) NOT NULL
        )""");
        System.out.println("Tags table created/checked");

        // Проверяем и заполняем теги
        CachedRowSet crs = dbConnect.executeQuery("SELECT COUNT(*) FROM tags");
        crs.next();
        int tagCount = crs.getInt(1);
        System.out.println("Found " + tagCount + " tags in database");

        if (tagCount == 0) {
            System.out.println("Inserting default tags...");
            dbConnect.executeUpdate("INSERT INTO tags VALUES (1, 'Electronics')");
            dbConnect.executeUpdate("INSERT INTO tags VALUES (2, 'Clothing')");
            dbConnect.executeUpdate("INSERT INTO tags VALUES (3, 'Home')");
        }

        // Создаем таблицу products
        dbConnect.executeUpdate("""
        CREATE TABLE IF NOT EXISTS products (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            quantity INT NOT NULL,
            tag_id INT NOT NULL,
            FOREIGN KEY (tag_id) REFERENCES tags(id)
        )""");
        System.out.println("Products table created/checked");
    }

    private void initializeSampleData() throws SQLException {
        CachedRowSet crs = dbConnect.executeQuery("SELECT COUNT(*) FROM products");
        crs.next();
        if (crs.getInt(1) == 0) {
            System.out.println("Inserting sample products...");

            // Вставляем тестовые данные
            String[] inserts = {
                    "INSERT INTO products (name, quantity, tag_id) VALUES ('Ноутбук', 10, 1)",
                    "INSERT INTO products (name, quantity, tag_id) VALUES ('Смартфон', 15, 1)",
                    "INSERT INTO products (name, quantity, tag_id) VALUES ('Футболка', 20, 2)"
            };

            for (String insert : inserts) {
                dbConnect.executeUpdate(insert);
            }
            System.out.println("Sample products inserted");
        }
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
            throw new RuntimeException("Error adding product", e);
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
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = dbConnect.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        System.out.println("Executing products query...");

        try {
            // Измененный запрос - убрали LEFT JOIN и используем только необходимые поля
            String sql = "SELECT id, name, quantity, tag_id FROM products";

            System.out.println("SQL: " + sql);

            try (CachedRowSet crs = dbConnect.executeQuery(sql)) {
                System.out.println("Query executed, processing results...");

                while (crs.next()) {
                    System.out.println("Processing row: " + crs.getInt("id"));

                    // Получаем тег по ID
                    int tagId = crs.getInt("tag_id");
                    Tag tag = tagDAO.getAllTags().stream()
                            .filter(t -> t.getId() == tagId)
                            .findFirst()
                            .orElse(new Tag(0, "Unknown"));

                    Product product = new Product(
                            crs.getInt("id"),
                            crs.getString("name"),
                            crs.getInt("quantity"),
                            tag
                    );
                    productList.add(product);
                }
            }
            System.out.println("Loaded " + productList.size() + " products");
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllProducts:");
            e.printStackTrace();
            throw new RuntimeException("Error getting products", e);
        }
        return productList;
    }

    @Override
    public void importFromCSV(String filePath) {
        throw new UnsupportedOperationException("Import from CSV not supported in database mode");
    }

    @Override
    public void exportToCSV(String filePath) {
        throw new UnsupportedOperationException("Export to CSV not supported in database mode");
    }

    @Override
    public void switchToDatabaseSource() {
        // Already in database mode
    }

    @Override
    public void switchToFileSource() {
        throw new UnsupportedOperationException("Cannot switch to file source in database mode");
    }
}
