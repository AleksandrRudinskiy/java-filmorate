ALTER TABLE film_category ADD FOREIGN KEY (film_id) REFERENCES films (film_id);

ALTER TABLE film_category ADD FOREIGN KEY (category_id) REFERENCES category (category_id);

ALTER TABLE film_genre ADD FOREIGN KEY (genre_id) REFERENCES genre (genre_id);

ALTER TABLE user_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE user_likes ADD FOREIGN KEY (film_id) REFERENCES films (film_id);

ALTER TABLE user_friends ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE film_genre ADD FOREIGN KEY (film_id) REFERENCES films (film_id);