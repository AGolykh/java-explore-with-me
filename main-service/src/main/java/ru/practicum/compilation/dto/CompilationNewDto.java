package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationNewDto {
    private boolean pinned;

    @NotNull
    @NotBlank
    @Length(max = 50)
    private String title;

    private Set<Long> events;
}
