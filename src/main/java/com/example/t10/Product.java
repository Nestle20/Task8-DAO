package com.example.t10;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private Tag tag;

    public Product(int id, String name, int quantity, Tag tag) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.tag = tag;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}