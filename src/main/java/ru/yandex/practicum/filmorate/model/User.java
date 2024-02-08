package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    private String name;
    @Email
    private String email;
    private String login;
    private LocalDate birthday;
    private Set<Long> friends;
}
