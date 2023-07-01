package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
public class CompilationUpdateDto {
    private Boolean pinned;

    @Size(max = 128)
    private String title;

    private Set<Long> events;
}
