package ru.yandex.practicum.filmorate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.FilmService;

@Configuration
@ComponentScan(basePackageClasses = FilmController.class)
public class FilmConfig {
    @Bean
    public FilmService getFilmService() {
        return new FilmService();
    }
}
