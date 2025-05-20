package com.example.t10;

import java.io.Serializable;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}