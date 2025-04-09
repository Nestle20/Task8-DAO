package com.example.t10;

import java.util.ArrayList;
import java.util.List;

public class TagListImpl implements TagDAO {
    private final List<Tag> tags;
    private int nextId = 1; // Счетчик для генерации ID

    public TagListImpl() {
        tags = new ArrayList<>();
        // Добавляем начальные теги
        tags.add(new Tag(nextId++, "Electronics"));
        tags.add(new Tag(nextId++, "Clothing"));
        tags.add(new Tag(nextId++, "Home"));
        tags.add(new Tag(nextId++, "Sports"));
        tags.add(new Tag(nextId++, "Books"));
    }
    @Override
    public List<Tag> getAllTags() {
        return new ArrayList<>(tags); // Возвращаем копию списка
    }
}