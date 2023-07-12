package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class EventSearchParamsAdminDto {
    private Set<Long> users;
    private Set<State> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;
}
