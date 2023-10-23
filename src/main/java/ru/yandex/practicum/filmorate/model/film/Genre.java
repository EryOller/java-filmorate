package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

@Data
public class Genre {
    private String name;
    private int id;

    public Genre(int id, String name) {
        this.name = name;
        this.id = id;
    }
}


