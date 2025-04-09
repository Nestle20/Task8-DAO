package com.example.t10;

import java.time.Year;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private Tag tag;
    private boolean isLeapYearProduct;

    public Product(int id, String name, int quantity, Tag tag) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.tag = tag;
        this.isLeapYearProduct = isLeapYear();
    }

    // Проверка, является ли текущий год високосным
    private boolean isLeapYear() {
        return Year.now().isLeap();
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isLeapYearProduct() {
        return isLeapYearProduct;
    }
}