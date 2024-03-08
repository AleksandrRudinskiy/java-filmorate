package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

@Data
@Builder
public class Review {
       Long reviewId;
       @NotBlank
       String content;
       @NotNull
       Boolean isPositive;
       @NotNull
       Long userId;
       @NotNull
       Long filmId;
       int useful;

       public Map<String, Object> toMap() {
              Map<String, Object> values = new HashMap<>();
              values.put("content", content);
              values.put("is_positive", isPositive);
              values.put("user_id", userId);
              values.put("film_id", filmId);
              return values;
       }
}