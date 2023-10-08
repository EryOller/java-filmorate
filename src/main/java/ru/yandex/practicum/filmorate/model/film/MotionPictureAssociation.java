package ru.yandex.practicum.filmorate.model.film;

import lombok.Getter;

@Getter
public enum MotionPictureAssociation {
    G("у фильма нет возрастных ограничений"),
    PG("детям рекомендуется смотреть фильм с родителями"),
    PG13("детям до 13 лет просмотр не желателен"),
    R("лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17("лицам до 18 лет просмотр запрещён");

    private String mpa;

    MotionPictureAssociation(String mpa) {
        this.mpa = mpa;
    }
}
