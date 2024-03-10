DROP TABLE IF EXISTS DIRECTOR_TO_FILM;
DROP TABLE IF EXISTS DIRECTOR;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS user_likes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS category;

CREATE TABLE IF NOT EXISTS films (
  film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  film_name varchar(100),
  description varchar(200),
  release_date date,
  duration integer,
  category_id integer
);

CREATE TABLE IF NOT EXISTS users (
  user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name varchar(100),
  email varchar(100),
  login varchar(100),
  birthday date
);

CREATE TABLE IF NOT EXISTS user_likes (
  film_id integer,
  user_id integer
);

CREATE TABLE IF NOT EXISTS user_friends (
  user_id integer,
  friend_id integer,
  friendship boolean
);

CREATE TABLE IF NOT EXISTS genre (
  genre_id integer PRIMARY KEY,
  genre_name varchar(50)
);

CREATE TABLE IF NOT EXISTS category (
  category_id integer PRIMARY KEY,
  category_name varchar(10)
);

CREATE TABLE IF NOT EXISTS film_genre (
  film_id integer,
  genre_id integer
);

CREATE TABLE IF NOT EXISTS review (
  review_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  content varchar,
  is_positive boolean,
  user_id integer,
  film_id integer
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id integer,
    user_id integer,
    is_like boolean
 );
 
CREATE TABLE IF NOT EXISTS DIRECTOR (
    DIRECTOR_ID   INTEGER auto_increment,
    DIRECTOR_NAME CHARACTER VARYING(20) NOT NULL,
    CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS DIRECTOR_TO_FILM (
    FILM_ID     INTEGER NOT NULL,
    DIRECTOR_ID INTEGER NOT NULL,
    PRIMARY KEY (FILM_ID, DIRECTOR_ID),
    CONSTRAINT DIRECTOR_TO_FILM_FILMS_FK
        FOREIGN KEY (FILM_ID) REFERENCES FILMS,
    CONSTRAINT DIRECTOR_TO_FILM_DIRECTOR_FK
        FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTOR
);

ALTER TABLE films ADD FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE;

ALTER TABLE film_genre ADD FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE;

ALTER TABLE user_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE user_likes ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE user_friends ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE film_genre ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE user_friends ADD FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE review ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE review ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE review_likes ADD FOREIGN KEY (review_id) REFERENCES review (review_id) ON DELETE CASCADE;

ALTER TABLE review_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;