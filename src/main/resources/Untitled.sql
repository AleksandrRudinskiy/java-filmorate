CREATE TABLE "films" (
  "film_id" integer PRIMARY KEY,
  "film_name" varchar,
  "description" varchar,
  "releaseDate" timestamp,
  "duration" integer
);

CREATE TABLE "users" (
  "user_id" integer PRIMARY KEY,
  "name" varchar,
  "email" varchar,
  "login" varchar,
  "birthday" date
);

CREATE TABLE "user_likes" (
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE "user_friends" (
  "user_id" integer,
  "friend_id" integer,
  "friendship" char(20)
);

CREATE TABLE "genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "film_category" (
  "film_id" integer,
  "category_id" integer
);

CREATE TABLE "category" (
  "category_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "film_genre" (
  "film_id" integer,
  "genre_id" integer
);

ALTER TABLE "film_category" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_category" ADD FOREIGN KEY ("category_id") REFERENCES "category" ("category_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id");

ALTER TABLE "user_likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "user_likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "user_friends" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");
