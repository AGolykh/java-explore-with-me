package ru.practicum.event.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Комментарий")
public class CommentDto {

    @Schema(description = "id комментария")
    private Long id;

    @Schema(description = "Текст комментария")
    private String text;

    private UserShortDto author;

    @Schema(description = "id события")
    private Long eventId;

    @Schema(description = "Дата создания")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
