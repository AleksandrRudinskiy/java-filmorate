# java-filmorate
![Схема базы данных](/scheme.png)
Template repository for Filmorate project.
## Основные запросы к БД:
1) Получение всех фильмов:
	SELECT *
	FROM films;
2) Получение всех пользователей:
   	SELECT *
   	FROM users;
3) Топ 10 названий наиболее популярных фильмов:  
	SELECT f.film_name, COUNT(user_id) as likes_count  
	FROM films AS f  
	LEFT JOIN user_likes AS ul ON f.film_id = ul.film_id
	GROUP BY f.film_name 
	ORDER BY likes_count DESC
	LIMIT 10;
4) Список общих друзей с другим пользователем:  
	SELECT *
	FROM
	(SELECT friend_id
	FROM user_friends
	WHERE user_id = 1) as t1
  INNER JOIN (SELECT friend_id
	FROM user_friends
	WHERE user_id = 2) as t2 ON t1.friend_id = t2.friend_id;
