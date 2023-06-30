package ru.practicum.compilation;

import lombok.*;
import ru.practicum.event.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    private boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}
