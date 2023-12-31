DROP TABLE IF EXISTS films, mpa, likes, genre_film, genre, users, friends, friendship;

CREATE TABLE IF NOT EXISTS films (
        film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(255) NOT NULL,
        description varchar(255),
        release_date date,
        duration integer NOT NULL,
        mpa_id INTEGER
);

CREATE TABLE IF NOT EXISTS mpa (
        mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS likes (
        film_id INTEGER NOT NULL,
        user_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_film (
        film_id INTEGER NOT NULL,
        genre_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
        genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
        user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        login varchar(255) NOT NULL,
        name varchar(255),
        email varchar(255) NOT NULL,
        birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
        user_id INTEGER NOT NULL,
        friend_id INTEGER NOT NULL,
        friendship_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship (
        friendship_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        status varchar(255) NOT NULL
);
