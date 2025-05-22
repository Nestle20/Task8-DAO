package com.example.t10;

import java.util.List;

public interface TagDAO {
    List<Tag> getAllTags();

    class Impl implements TagDAO {
        private final List<Tag> tags = List.of(
                new Tag(1, "Электроника"),
                new Tag(2, "Одежда"),
                new Tag(3, "Дом")
        );

        @Override
        public List<Tag> getAllTags() {
            return tags;
        }
    }
}