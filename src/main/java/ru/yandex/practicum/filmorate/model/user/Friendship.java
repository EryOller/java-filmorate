package ru.yandex.practicum.filmorate.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class Friendship {
    private Long user;
    private StatusFrindship status;

    public Friendship(Long user, StatusFrindship statusFrindship) {
        this.user = user;
        this.status = statusFrindship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(user, that.user) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, status);
    }
}
