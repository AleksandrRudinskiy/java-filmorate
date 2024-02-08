INSERT INTO users VALUES (1,'Alexandr', 'alex@yandex.ru', 'alex07', '1990-12-30');
INSERT INTO users VALUES (2,'Boris', 'bobr@mail.ru', 'bob15', '1970-05-09');
INSERT INTO users VALUES (3,'Anna', 'anyuta@gmail.com', 'anet77', '1995-08-13');

INSERT INTO films VALUES (1, 'От заката до рассвета', 'Тут какое-то описание', '1990-10-01', 95);
INSERT INTO films VALUES (2, 'Матрица', 'Описание...', '1995-08-07', 110);
INSERT INTO films VALUES (3, 'Рэмбо', 'Описание фильма', '1987-02-02', 133);

INSERT INTO genre VALUES (1, 'Комедия');
INSERT INTO genre VALUES (2, 'Драма');
INSERT INTO genre VALUES (3, 'Мультфильм');
INSERT INTO genre VALUES (4, 'Триллер');
INSERT INTO genre VALUES (5, 'Документальный');
INSERT INTO genre VALUES (6, 'Боевик');

INSERT INTO film_genre VALUES (1, 4);
INSERT INTO film_genre VALUES (1, 6);
INSERT INTO film_genre VALUES (2, 6);
INSERT INTO film_genre VALUES (3, 6);
INSERT INTO film_genre VALUES (3, 1);

INSERT INTO user_likes VALUES (1, 1);
INSERT INTO user_likes VALUES (1, 2);
INSERT INTO user_likes VALUES (1, 3);
INSERT INTO user_likes VALUES (2, 3);
INSERT INTO user_likes VALUES (2, 2);
INSERT INTO user_likes VALUES (3, 1);

INSERT INTO user_friends VALUES (1,2,'подтверждённая');
INSERT INTO user_friends VALUES (1,3,'подтверждённая');
INSERT INTO user_friends VALUES (2,3,'подтверждённая');




