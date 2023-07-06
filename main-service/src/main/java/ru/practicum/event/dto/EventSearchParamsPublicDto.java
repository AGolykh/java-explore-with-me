package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class EventSearchParamsPublicDto {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortType sort;
    private Integer from;
    private Integer size;

    public enum SortType {
        EVENT_DATE,
        VIEWS
    }
}
