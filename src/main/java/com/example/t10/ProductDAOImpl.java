package com.example.t10;

import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private final List<Product> products = new ArrayList<>();
    private final TagDAO tagDAO;

    public ProductDAOImpl(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    @Override
    public void addProduct(Product product) {
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
        products.removeIf(p -> p.getId() == id);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public void importFromCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void exportToCSV(String filePath) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getCurrentFilePath() {
        return null;
    }
}