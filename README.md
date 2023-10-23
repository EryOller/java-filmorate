# database-schema
Схема базы данных Filmorate

![BD.JPG](src%2Fmain%2Fresources%2FBD.JPG)


---

## Основные сущности
- Фильмы (films)
- Пользователи (users)
- Жанр фильма (genre)
- Лайки к фильму (likes)
- Рейтинг ассоциации кинокомпаний (mpa)
- Друзья (friends)


## Запросы к БД
*Получить фильмы с ограничением 18+*
- SELECT f.name AS Фильмы, m.name AS Ограничение
  FROM films AS f
  INNER JOIN mpa AS m ON f.mpa_id = m.mpa.id
  WHERE m.name = 'NC-17';

*Узнать количество лайков у фильма Аватар*
- SELECT f.name AS Фильм, COUNT(l.user_id) AS Лайки
  FROM films AS f
  INNER JOIN likes AS l ON f.film_id=l.film_id
  GROUP BY f.film_id
  WHERE f.name = 'Аватар';

*Посмотреть всех друзей у пользователя EryOller*
-  SELECT *
   FROM users
   WHERE user_id IN (
   SELECT f.friend_id
   FROM users AS u
   INNER JOIN friends AS f ON u.user_id = f.user_id
   INNER JOUN friendship AS fs f.friendship_id = fs.friendship_id
   WHERE fs.status = 'подтверждённая' AND u.login = 'EryOller');
