package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(NamedParameterJdbcOperations namedParameterJdbcOperations, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres;

        final String sqlGetFilms = "SELECT genre_id, name FROM genre;";
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlGetFilms);
        genres = makeListGenres(genresRows);
        return genres;
    }

    @Override
    public boolean hasKeyInStorage(int id) {
        final String sqlGetGenreById = "SELECT genre_id FROM genre WHERE genre_id = :genreId";
        SqlRowSet hasGenre = namedParameterJdbcOperations.queryForRowSet(sqlGetGenreById, Map.of("genreId", id));
        return hasGenre.next();
    }

    @Override
    public Genre getGenreFromStorageById(int id) {
        final String sqlGetGenreById = "SELECT genre_id, name FROM genre WHERE genre_id = :genreId;";
        SqlRowSet genreRows = namedParameterJdbcOperations.queryForRowSet(sqlGetGenreById, Map.of("genreId", id));
        return makeGenre(genreRows);
    }

    private static List<Genre> makeListGenres(SqlRowSet genreResultSet) {
        Genre genre;
        List<Genre> genres = new ArrayList<>();

        while (genreResultSet.next()) {
            genre = new Genre(genreResultSet.getInt("GENRE_ID"), genreResultSet.getString("NAME"));
            genres.add(genre);
        }
        return genres;
    }

    private static Genre makeGenre(SqlRowSet genreResultSet) {
        Genre genre =  null;
        while (genreResultSet.next()) {
            genre = new Genre(genreResultSet.getInt("GENRE_ID"), genreResultSet.getString("NAME"));
        }
        return genre;
    }

}
