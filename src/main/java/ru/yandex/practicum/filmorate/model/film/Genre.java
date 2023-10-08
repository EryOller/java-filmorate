package ru.yandex.practicum.filmorate.model.film;

import lombok.Getter;

@Getter
public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    ANIMATION("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private String genre;
    Genre(String genre) {
        this.genre = genre;
    }
}
