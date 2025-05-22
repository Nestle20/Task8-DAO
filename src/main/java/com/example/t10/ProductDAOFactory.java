package com.example.t10;

public class ProductDAOFactory {
    public static ProductDAO createProductDAO(String dataSourceType, TagDAO tagDAO) {
        switch (dataSourceType) {
            case "H2 Database":
                return new ProductDBConnectDAO(tagDAO);
            case "CSV File":
                return new ProductCSVDAO(tagDAO);
            case "In-Memory":
                return new ProductInMemoryDAO(tagDAO);
            default:
                throw new IllegalArgumentException("Unknown data source type: " + dataSourceType);
        }
    }
}