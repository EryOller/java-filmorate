package ru.yandex.practicum.filmorate.model.user;

import lombok.Getter;

@Getter
public enum StatusFrindship {
    CONFIRMED("подтверждённая"),
    UNCONFIRMED("неподтверждённая");

    private String status;

    StatusFrindship(String status) {
        this.status = status;
    }
}
