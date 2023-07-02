package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationNewDto {
    private boolean pinned;

    @NotBlank
    @Size(max = 255)
    private String title;

    private Set<Long> events;
}
