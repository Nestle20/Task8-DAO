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
            dbConnect.connect(false);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = dbConnect.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, quantity, tag_id FROM products";

        try (CachedRowSet crs = dbConnect.executeQuery(sql)) {
            while (crs.next()) {
                int tagId = crs.getInt("tag_id");
                Tag tag = tagDAO.getAllTags().stream()
                        .filter(t -> t.getId() == tagId)
                        .findFirst()
                        .orElse(null);

                if (tag != null) {
                    products.add(new Product(
                            crs.getInt("id"),
                            crs.getString("name"),
                            crs.getInt("quantity"),
                            tag
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void importFromCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Import from CSV not supported for database");
    }

    @Override
    public void exportToCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Export to CSV not supported for database");
    }

    @Override
    public String getCurrentFilePath() {
        return null;
    }
}