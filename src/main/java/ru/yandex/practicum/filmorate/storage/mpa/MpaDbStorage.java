package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final JdbcTemplate jdbcTemplate;


    public MpaDbStorage(NamedParameterJdbcOperations namedParameterJdbcOperations, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpa() {
        List<Mpa> genres;

        final String sqlGetMpa = "SELECT mpa_id, name FROM mpa;";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlGetMpa);
        genres = makeListMpas(mpaRows);
        return genres;
    }

    @Override
    public boolean hasKeyInStorage(int id) {
        final String sqlGetMpaById = "SELECT mpa_id, name FROM mpa WHERE mpa_id = :mpaId;";
        SqlRowSet hasMpa = namedParameterJdbcOperations.queryForRowSet(sqlGetMpaById, Map.of("mpaId", id));
        return hasMpa.next();
    }

    @Override
    public Mpa getMpaFromStorageById(int id) {
        final String sqlGetMpaById = "SELECT mpa_id, name FROM mpa WHERE mpa_id = :mpaId;";
        SqlRowSet mpaRows = namedParameterJdbcOperations.queryForRowSet(sqlGetMpaById, Map.of("mpaId", id));
        List<Mpa> mpas = makeListMpas(mpaRows);
        return mpas.get(mpas.size() - 1);
    }

    private static List<Mpa> makeListMpas(SqlRowSet mpaResultSet) {
        Mpa mpa;
        List<Mpa> mpas = new ArrayList<>();

        while (mpaResultSet.next()) {
            mpa = new Mpa(mpaResultSet.getInt("MPA_ID"), mpaResultSet.getString("NAME"));
            mpas.add(mpa);
        }
        return mpas;
    }
}
