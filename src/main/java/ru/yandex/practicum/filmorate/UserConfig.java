package ru.yandex.practicum.filmorate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.UserService;

@Configuration
@ComponentScan(basePackageClasses = UserController.class)
public class UserConfig {
    @Bean
    public UserService getUserService() {
        return new UserService();
    }
}
