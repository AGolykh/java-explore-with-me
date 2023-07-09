package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {
    private boolean pinned;

    @Length(max = 50)
    private String title;

    private Set<Long> events;
}
