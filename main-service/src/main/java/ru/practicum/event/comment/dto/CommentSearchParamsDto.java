package ru.practicum.event.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Параметры для поиска")
public class CommentSearchParamsDto {
    @Schema(description = "Текст, присутствующий в комментарии")
    private String text;

    @Schema(description = "Список id пользователей, чьи комментарии нужно найти")
    private Set<Long> users;

    @Schema(description = "Список id эвентов, в которых будет вестись поиск")
    private Set<Long> events;

    @Schema(description = "Дата и время не раньше которых должен быть опубликован комментарий")
    private LocalDateTime rangeStart;

    @Schema(description = "Дата и время не позже которых должен быть опубликован комментарий")
    private LocalDateTime rangeEnd;

    @Schema(description = "Количество комментариев, которые нужно пропустить для формирования текущего набора")
    private Integer from;

    @Schema(description = "Количество комментариев в наборе")
    private Integer size;
}
