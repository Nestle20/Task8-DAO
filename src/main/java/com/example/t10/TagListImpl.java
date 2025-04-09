package com.example.t10;

import java.util.ArrayList;
import java.util.List;

public class TagListImpl implements TagDAO {
    private final List<Tag> tags;

    public TagListImpl() {
        tags = new ArrayList<>();
        tags.add(new Tag(1, "Electronics"));
        tags.add(new Tag(2, "Clothing"));
        tags.add(new Tag(3, "Home"));

        // Добавляем тег по умолчанию на случай ошибок
        tags.add(new Tag(0, "Unknown"));
    }

    @Override
    public List<Tag> getAllTags() {
        return new ArrayList<>(tags);
    }
}