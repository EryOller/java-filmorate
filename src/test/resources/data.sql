INSERT INTO users (login, name, email, birthday) VALUES('tuk-tuk', 'Дима', 'dima@mail.ru', '1990-10-10');
INSERT INTO users (login, name, email, birthday) VALUES('tik-tak', 'Митя', 'mitay@mail.ru', '2000-11-09');
INSERT INTO users (login, name, email, birthday) VALUES('tur', 'Гоша', 'gosha@mail.ru', '1985-01-10');
INSERT INTO users (login, name, email, birthday) VALUES('Z', 'Рома', 'roma@mail.ru', '1985-12-30');
INSERT INTO users (login, name, email, birthday) VALUES('super', 'Света', 'sveta@mail.ru', '2010-01-01');

INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(1, 2, 1);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(2, 1, 1);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(3, 2, 1);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(2, 3, 1);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(3, 4, 2);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(3, 5, 2);
INSERT INTO friends (user_id, friend_id, friendship_id) VALUES(5, 1, 2);

INSERT INTO likes (film_id, user_id) VALUES(1, 1);
INSERT INTO likes (film_id, user_id) VALUES(1, 2);
INSERT INTO likes (film_id, user_id) VALUES(2, 3);

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES('Мстители 1', 'Первый фильм', '2004-01-01', 1000, 1);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES('Мстители 2', 'Второй фильм', '2007-01-01', 2000, 2);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES('Золушка', 'Для девочек', '1996-01-01', 3000, 3);
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES('Матрица', 'Для мальчиков', '2004-01-01', 1000, 4);


INSERT INTO genre_film (film_id, genre_id) VALUES(1, 3);
INSERT INTO genre_film (film_id, genre_id) VALUES(1, 1);
INSERT INTO genre_film (film_id, genre_id) VALUES(2, 2);
INSERT INTO genre_film (film_id, genre_id) VALUES(2, 4);
INSERT INTO genre_film (film_id, genre_id) VALUES(3, 5);
INSERT INTO genre_film (film_id, genre_id) VALUES(4, 6);
