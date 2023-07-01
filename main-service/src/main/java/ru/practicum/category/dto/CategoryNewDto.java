package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
public class CategoryNewDto {
    @NotBlank
    @Size(max = 255)
    private String name;
}
