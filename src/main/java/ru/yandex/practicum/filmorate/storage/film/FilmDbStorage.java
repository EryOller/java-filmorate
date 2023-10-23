package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final JdbcTemplate jdbcTemplate;


    public FilmDbStorage(NamedParameterJdbcOperations namedParameterJdbcOperations, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        final String sqlMpaId = "SELECT name FROM mpa WHERE mpa_id = :mpaId";
        final String sqlGenreFilm = "INSERT INTO genre_film (film_id, genre_id) VALUES (:filmId, :genreId);";
        final String sqlLikes = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId);";
        final String sqlFilm = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (:name, :description, :releaseDate, :duration, :mpaId);";
        final String sqlGenreName = "SELECT name FROM genre WHERE genre_id = :genreId;";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        Set<Genre> genres = new HashSet<>();
        SqlRowSet genreRows;
        SqlRowSet mpaRows = namedParameterJdbcOperations.queryForRowSet(sqlMpaId, Map.of("mpaId", film.getMpa().getId()));
        mpaRows.next();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreRows = namedParameterJdbcOperations.queryForRowSet(sqlGenreName, Map.of("genreId", genre.getId()));
                genreRows.next();
                genres.add(new Genre(genre.getId(), genreRows.getString("NAME")));
            }
        }
        film.setGenres(genres);
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("duration",  film.getDuration());
        map.addValue("mpaId",  film.getMpa().getId());
        map.addValue("description", film.getDescription());
        namedParameterJdbcOperations.update(sqlFilm, map, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        for (Genre genre : film.getGenres()) {
            namedParameterJdbcOperations.update(sqlGenreFilm, Map.of("filmId", film.getId(),
                    "genreId", genre.getId()));
        }
        for (Long like : film.getLikesByUsers()) {
            namedParameterJdbcOperations.update(sqlLikes, Map.of("filmId",  film.getId(), "userId", like));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        final String sqlMpaName = "SELECT name FROM mpa WHERE mpa_id = :mpaId";
        final String sqlUpdateFilmFieldName = "UPDATE films SET name = :name " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldDescriprion = "UPDATE films SET description = :description " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldReleaseDate = "UPDATE films SET release_date = :releaseDate " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldDuration = "UPDATE films SET duration = :duration " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldMpa = "UPDATE films SET mpa_id = :mpa " +
                "WHERE film_id = :filmId;";
        final String sqlDeleteLikesByFilmId = "DELETE FROM likes WHERE film_id = :filmId;";
        final String sqlLikes = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId);";
        final String sqlGenreName = "SELECT name FROM genre WHERE genre_id = :genreId;";
        final String sqlGenreFilm = "INSERT INTO genre_film (film_id, genre_id) VALUES (:filmId, :genreId);";
        final String sqlDeleteGenreFilmByFilmId = "DELETE FROM genre_film WHERE film_id = :filmId;";
        SqlRowSet mpaRows = namedParameterJdbcOperations.queryForRowSet(sqlMpaName,
                Map.of("mpaId", film.getMpa().getId()));
        mpaRows.next();
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldName, Map.of("name", film.getName(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldDescriprion,
                Map.of("description", film.getDescription(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldReleaseDate,
                Map.of("releaseDate", film.getReleaseDate(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldDuration, Map.of("duration", film.getDuration(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldMpa, Map.of("mpa", film.getMpa().getId(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlDeleteLikesByFilmId, Map.of("filmId", film.getId()));
        for (Long like : film.getLikesByUsers()) {
            namedParameterJdbcOperations.update(sqlLikes, Map.of("filmId",  film.getId(),
                    "userId", like));
        }
        namedParameterJdbcOperations.update(sqlDeleteGenreFilmByFilmId, Map.of("filmId", film.getId()));
        SqlRowSet genreRows;

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreRows = namedParameterJdbcOperations.queryForRowSet(sqlGenreName, Map.of("genreId", genre.getId()));
                genreRows.next();
                namedParameterJdbcOperations.update(sqlGenreFilm, Map.of("filmId", film.getId(),
                        "genreId", genre.getId()));
            }
        }
        return getFilmFromStorageById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films;
        SqlRowSet likesFilmByFilmId;
        SqlRowSet genresFilmByFilmId;

        final String sqlGetFilms = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration," +
                "m.mpa_id mpaid, m.name mpa " +
                "FROM films AS f LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id;";
        final String sqlGetGenres = "SELECT g.genre_id, g.name " +
                "FROM genre_film AS gf LEFT OUTER JOIN genre AS g ON gf.genre_id = g.genre_id " +
                "WHERE gf.film_id = :filmId;";
        final String sqlGetLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = :filmId;";
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(sqlGetFilms);
        films = makeListFilms(filmsRows);
        if (films != null) {
            for (Film film : films) {
                genresFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetGenres, Map.of("filmId", film.getId()));
                film.setGenres(makeGenres(genresFilmByFilmId));
                likesFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetLikesByFilmId, Map.of("filmId", film.getId()));
                film.setLikesByUsers(makeLikes(likesFilmByFilmId));
            }
        }

        return films;
    }

    @Override
    public boolean hasKeyInStorage(Long id) {
        final String sqlGetFilmById = "SELECT film_id FROM films WHERE film_id = :filmId";
        SqlRowSet hasfilm = namedParameterJdbcOperations.queryForRowSet(sqlGetFilmById, Map.of("filmId", id));
        return hasfilm.next();
    }

    @Override
    public Film getFilmFromStorageById(Long id) {
        Film film;
        final String sqlGetFilmById = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration," +
                "m.mpa_id mpaid, m.name mpa " +
                "FROM films AS f LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = :filmId;";
        final String sqlGetGenres = "SELECT g.genre_id, g.name " +
                "FROM genre_film AS gf LEFT OUTER JOIN genre AS g ON gf.genre_id = g.genre_id " +
                "WHERE gf.film_id = :filmId ORDER BY g.genre_id;";
        final String sqlGetLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = :filmId;";
        SqlRowSet filmRows = namedParameterJdbcOperations.queryForRowSet(sqlGetFilmById, Map.of("filmId", id));
        List<Film> films = makeListFilms(filmRows);
        SqlRowSet likesFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetLikesByFilmId, Map.of("filmId", id));
        film = films.get(films.size() - 1);
        SqlRowSet genresFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetGenres, Map.of("filmId", film.getId()));
        film.setLikesByUsers(makeLikes(likesFilmByFilmId));
        film.setGenres(makeGenres(genresFilmByFilmId));
        return film;
    }

    private static List<Film> makeListFilms(SqlRowSet filmResultSet) {
        Film film;
        List<Film> films = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        while (filmResultSet.next()) {
            film = new Film(filmResultSet.getString("NAME"), filmResultSet.getInt("DURATION"));
            film.setId(filmResultSet.getLong("FILM_ID"));
            film.setDescription(filmResultSet.getString("DESCRIPTION"));
            film.setReleaseDate(LocalDate.parse(filmResultSet.getString("RELEASE_DATE"), formatter));
            film.setMpa(new Mpa(filmResultSet.getInt("MPAID"), filmResultSet.getString("MPA")));
            films.add(film);
        }
        return films;
    }

    private static Set<Long> makeLikes(SqlRowSet likesByFilmResultSet) {
        Set<Long> likesfilm = new HashSet<>();
        while (likesByFilmResultSet.next()) {
            likesfilm.add(likesByFilmResultSet.getLong("USER_ID"));
        }

        return likesfilm;
    }

    private static Set<Genre> makeGenres(SqlRowSet genresByFilmResultSet) {
        Set<Genre> genresfilm = new HashSet<>();
        while (genresByFilmResultSet.next()) {
            genresfilm.add(new Genre(genresByFilmResultSet.getInt("GENRE_ID"),
                    genresByFilmResultSet.getString("NAME")));
        }

        return genresfilm;
    }
}
