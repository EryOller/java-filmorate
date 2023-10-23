package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBFilmRepositoryTest {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void testFindFilmById() {
        Film film = filmStorage.getFilmFromStorageById(2L);
        assertNotNull(film);
        assertEquals(2L, film.getId());
        assertEquals("Мстители 2", film.getName());
    }

    @Test
    public void testHasFilmById() {
        boolean hasKeyInStorage = filmStorage.hasKeyInStorage(3L);
        Film film = filmStorage.getFilmFromStorageById(3L);
        assertNotNull(film);
        assertEquals(3L, film.getId());
        assertEquals(true, hasKeyInStorage);
    }

    @Test
    public void testSaveUserAndGetAllUsers() {
        Film film = new Film("Выжить любой ценой", 4000);
        film.setDescription("Выжить в Сибире");
        Set<Genre> genres = new HashSet<>();
        genres.add(genreDbStorage.getGenreFromStorageById(1));
        genres.add(genreDbStorage.getGenreFromStorageById(2));
        film.setGenres(genres);
        film.setReleaseDate(LocalDate.of(2011,01,01));
        film.setMpa(mpaDbStorage.getMpaFromStorageById(1));
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);
        likes.add(3L);
        film.setLikesByUsers(likes);
        int countFilmsBeforeAdd = filmStorage.getFilms().size();
        filmStorage.save(film);
        int countFilmsAfterAdd = filmStorage.getFilms().size();
        assertEquals(countFilmsAfterAdd, countFilmsBeforeAdd + 1);

        Film filmCurrent = filmStorage.getFilmFromStorageById(Long.valueOf(countFilmsAfterAdd));
        assertNotNull(filmCurrent);
        assertEquals("Выжить любой ценой", filmCurrent.getName());
    }

    @Test
    public void testUpdateUser() {
        Film film = new Film("ТОП ГИР", 4000);
        film.setDescription("Гонка леман");
        Set<Genre> genres = new HashSet<>();
        genres.add(genreDbStorage.getGenreFromStorageById(3));
        genres.add(genreDbStorage.getGenreFromStorageById(4));
        film.setGenres(genres);
        film.setReleaseDate(LocalDate.of(2011,01,01));
        film.setMpa(mpaDbStorage.getMpaFromStorageById(2));
        Set<Long> likes = new HashSet<>();
        likes.add(1L);
        likes.add(2L);
        likes.add(3L);
        film.setLikesByUsers(likes);
        filmStorage.save(film);
        int countFilms = filmStorage.getFilms().size();
        film.setName("Пятая передача");
        film.setDescription("Гонка леман на выживание");
        filmStorage.update(film);
        Film filmCurrent = filmStorage.getFilmFromStorageById(Long.valueOf(countFilms));

        assertNotNull(filmCurrent);
        assertEquals("Пятая передача", film.getName());
        assertEquals("Гонка леман на выживание", film.getDescription());
    }
}
