package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private String login;
    @Email
    private String email;
    private String name;
    private LocalDate birthday;
    private int id;
}
