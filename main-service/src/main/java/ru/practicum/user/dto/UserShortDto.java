package ru.practicum.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пользователь")
public class UserShortDto {
    @Schema(description = "id пользователя")
    private Long id;

    @Schema(description = "Имя пользователя")
    private String name;
}
