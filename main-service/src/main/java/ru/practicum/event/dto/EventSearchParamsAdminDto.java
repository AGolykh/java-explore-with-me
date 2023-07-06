package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EventSearchParamsAdminDto {
    private Set<Long> users;
    private Set<State> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    public EventSearchParamsAdminDto(Set<Long> users,
                                     Set<State> states,
                                     Set<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Integer from,
                                     Integer size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.from = from;
        this.size = size;
    }
}
