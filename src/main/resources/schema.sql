CREATE TABLE IF NOT EXISTS films (
  film_id integer PRIMARY KEY,
  film_name varchar,
  description varchar,
  releaseDate date,
  duration integer
);

CREATE TABLE IF NOT EXISTS users (
  user_id integer PRIMARY KEY,
  name varchar,
  email varchar,
  login varchar,
  birthday timestamp
);

CREATE TABLE IF NOT EXISTS user_likes (
  film_id integer,
  user_id integer
);

CREATE TABLE IF NOT EXISTS user_friends (
  user_id integer,
  friend_id integer,
  friendship char(20)
);

CREATE TABLE IF NOT EXISTS genre (
  genre_id integer PRIMARY KEY,
  name varchar
);

CREATE TABLE IF NOT EXISTS film_category (
  film_id integer,
  category_id integer
);

CREATE TABLE IF NOT EXISTS category (
  category_id integer PRIMARY KEY,
  name varchar
);

CREATE TABLE IF NOT EXISTS film_genre (
  film_id integer,
  genre_id integer
);


