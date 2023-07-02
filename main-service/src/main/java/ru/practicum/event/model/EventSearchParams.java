package ru.practicum.event.model;

import lombok.Data;
import ru.practicum.validator.StartEndDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@StartEndDate
public class EventSearchParams {
    private Set<Long> users;
    private Set<State> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;
    public DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventSearchParams(Set<Long> users,
                             Set<State> states,
                             Set<Long> categories,
                             String rangeStart,
                             String rangeEnd,
                             Integer from,
                             Integer size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = LocalDateTime.parse(rangeStart, FORMATTER);
        this.rangeEnd = LocalDateTime.parse(rangeEnd, FORMATTER);
        this.from = from;
        this.size = size;
    }
}
