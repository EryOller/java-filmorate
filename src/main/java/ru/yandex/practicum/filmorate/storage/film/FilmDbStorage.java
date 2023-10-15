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
import ru.yandex.practicum.filmorate.model.film.MotionPictureAssociation;

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
        final String sqlMpaId = "SELECT mpa_id FROM mpa WHERE name = :mpa";
        final String sqlGenreFilm = "INSERT INTO genre_film (film_id, genre_id) VALUES (:filmId, :genreId);";
        final String sqlLikes = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId);";
        final String sqlFilm = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (:name, :description, :releaseDate, :duration, :mpaId);";
        final String sqlGenreId = "SELECT genre_id FROM genre WHERE name = :genre;";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        SqlRowSet genreRows;
        SqlRowSet mpaRows = namedParameterJdbcOperations.queryForRowSet(sqlMpaId, Map.of("mpa", film.getMpa().getMpa()));
        mpaRows.next();
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("releaseDate", film.getReleaseDate());
        map.addValue("duration",  film.getDuration());
        map.addValue("mpaId",  mpaRows.getInt("MPA_ID"));
        map.addValue("description", film.getDescription());
        namedParameterJdbcOperations.update(sqlFilm, map, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        for (Genre genre : film.getGenres()) {
            genreRows = namedParameterJdbcOperations.queryForRowSet(sqlGenreId, Map.of("genre", genre.getGenre()));
            genreRows.next();
            namedParameterJdbcOperations.update(sqlGenreFilm, Map.of("filmId", film.getId(),
                    "genreId", genreRows.getString("genre_id")));
        }
        for (Long like : film.getLikesByUsers()) {
            namedParameterJdbcOperations.update(sqlLikes, Map.of("filmId",  film.getId(), "userId", like));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        final String sqlMpaId = "SELECT mpa_id FROM mpa WHERE name = :mpa";
        final String sqlUpdateFilmFieldName = "UPDATE films SET name = :name " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldDescriprion = "UPDATE films SET description = :description" +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldReleaseDate = "UPDATE films SET release_date = :releaseDate " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldDuration = "UPDATE films SET duration = :duration " +
                "WHERE film_id = :filmId;";
        final String sqlUpdateFilmFieldMpa = "UPDATE films SET mpa_id = :mpa " +
                "WHERE film_id = :filmId;";
        final String sqlDeleteLikesByFilmId = "DELETE FROM likes WHERE film_id = :filmId;";
        final String sqlLikes = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId);";
        final String sqlGenreId = "SELECT genre_id FROM genre WHERE name = :genre;";
        final String sqlGenreFilm = "INSERT INTO genre_film (film_id, genre_id) VALUES (:filmId, :genreId);";

        SqlRowSet mpaRows = namedParameterJdbcOperations.queryForRowSet(sqlMpaId,
                Map.of("mpa", film.getMpa().getMpa()));
        mpaRows.next();
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldName,
                Map.of("name", film.getName(), "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldDescriprion,
                Map.of("description", film.getDescription(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldReleaseDate,
                Map.of("releaseDate", film.getReleaseDate(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldDuration, Map.of("duration", film.getDuration(),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlUpdateFilmFieldMpa, Map.of("mpa", mpaRows.getInt("MPA_ID"),
                "filmId", film.getId()));
        namedParameterJdbcOperations.update(sqlDeleteLikesByFilmId, Map.of("filmId", film.getId()));
        for (Long like : film.getLikesByUsers()) {
            namedParameterJdbcOperations.update(sqlLikes, Map.of("filmId",  film.getId(),
                    "userId", like));
        }

        final String sqlDeleteGenreFilmByFilmId = "DELETE FROM genre_film WHERE film_id = :filmId;";
        namedParameterJdbcOperations.update(sqlDeleteGenreFilmByFilmId, Map.of("filmId", film.getId()));
        SqlRowSet genreRows;

        for (Genre genre : film.getGenres()) {
            genreRows = namedParameterJdbcOperations.queryForRowSet(sqlGenreId, Map.of("genre", genre.getGenre()));
            genreRows.next();
            namedParameterJdbcOperations.update(sqlGenreFilm, Map.of("filmId", film.getId(),
                    "genreId", genreRows.getString("genre_id")));
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films;
        SqlRowSet likesFilmByFilmId ;

        final String sqlGetFilms = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.name mpa " +
                "FROM films AS f LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id;";
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(sqlGetFilms);
        final String sqlGetLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = :filmId;";

        films = makeListFilms(filmsRows);
        for (Film film : films) {
            likesFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetLikesByFilmId, Map.of("filmId", film.getId()));
            makeLikes(likesFilmByFilmId);
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
                "m.name mpa " +
                "FROM films AS f LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
                "HAVING f.film_id = :filmId;";
        final String sqlGetLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = :filmId;";

        SqlRowSet filmRows = namedParameterJdbcOperations.queryForRowSet(sqlGetFilmById, Map.of("filmId", id));
        SqlRowSet likesFilmByFilmId = namedParameterJdbcOperations.queryForRowSet(sqlGetLikesByFilmId, Map.of("filmId", id));
        List<Film> films = makeListFilms(filmRows);
        film = films.get(films.size() - 1);
        film.setLikesByUsers(makeLikes(likesFilmByFilmId));
        return film;
    }

    private static List <Film> makeListFilms(SqlRowSet filmResultSet) {
        Film film;
        List<Film> films = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.d");
        while (filmResultSet.next()) {
            film = new Film(filmResultSet.getString("NAME"), filmResultSet.getInt("DURATION"));
            film.setId(filmResultSet.getLong("FILM_ID"));
            film.setName(filmResultSet.getString("DESCRIPTION"));
            film.setReleaseDate(LocalDate.parse(filmResultSet.getString("RELEASE_DATE"), formatter));
            film.setMpa(
                    Arrays.stream(MotionPictureAssociation.values())
                            .filter(m -> m.getMpa().equals(filmResultSet.getString("MPA")))
                            .findFirst()
                            .get());
            films.add(film);
        }
        return films;
    }

    private static Set<Long> makeLikes(SqlRowSet likesByFilmResultSet) {
        Set<Long> likesfilm = null;
        while (likesByFilmResultSet.next()) {
            likesfilm.add(likesByFilmResultSet.getLong("USER_ID"));
        }

        return likesfilm;
    }
}
