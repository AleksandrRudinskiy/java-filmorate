INSERT INTO category SELECT 1, 'G' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = 1);
INSERT INTO category SELECT 2, 'PG' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = 2);
INSERT INTO category SELECT 3, 'PG-13' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = 3);
INSERT INTO category SELECT 4, 'R' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = 4);
INSERT INTO category SELECT 5, 'NC-17' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = 5);


INSERT INTO genre SELECT 1, 'Комедия' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 1);
INSERT INTO genre SELECT 2, 'Драма' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 2);
INSERT INTO genre SELECT 3, 'Мультфильм' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 3);
INSERT INTO genre SELECT 4, 'Триллер' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 4);
INSERT INTO genre SELECT 5, 'Документальный' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 5);
INSERT INTO genre SELECT 6, 'Боевик' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = 6);


--INSERT INTO user_likes VALUES (1, 1);
--INSERT INTO user_likes VALUES (1, 2);
--INSERT INTO user_likes VALUES (1, 3);

--INSERT INTO user_likes VALUES (2, 1);
--INSERT INTO user_likes VALUES (2, 2);

--INSERT INTO user_likes VALUES (1, 1);