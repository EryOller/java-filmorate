package ru.yandex.practicum.filmorate.model.film;


import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validator.RealiseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank(message = "Передано название фильма из пробелов")
    @NotEmpty(message = "Передано пустое название фильма")
    @NonNull
    private String name;
    @Size(max = 200, message = "Описание фильне превысило 200 символов")
    private String description;
    @RealiseDateConstraint(message = "Дата фильма оказалась раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @NonNull
    @Positive(message = "Продолжительность фильма может быть только положительным значением")
    private int duration;
    private Set<Long> likesByUsers = new HashSet<>();
    private List<Genre> genres;
    private MotionPictureAssociation mpa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(name, film.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Set<Long> getListLikesByUsers() {
        return likesByUsers;
    }

}
