package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NonNull
    @Email(message = "Почта должна соотвествовать структуре написанию электронного почтового адреса")
    private String email;
    @NonNull
    @Pattern(regexp = "^[\\S]+.$", message = "В логине содержутся пробелы")
    private String login;
    private String name;
    @NonNull
    @Past(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Friendship> friends = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, login);
    }

    public Set<Friendship> getListFriends() {
        return friends;
    }
}
