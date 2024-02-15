package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data

public class User {

    private long id;
    private String name;
    @Email
    private String email;
    private String login;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }

    public User(long id, String name, String email, String login, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}
