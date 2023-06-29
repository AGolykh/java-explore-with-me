package ru.practicum.stats.model;

import lombok.Data;
import ru.practicum.stats.validator.StartEndDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@StartEndDate
public class DateRange {

    private final LocalDateTime start;
    private final LocalDateTime end;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DateRange(String start, String end) {
        this.start = LocalDateTime.parse(start, FORMATTER);
        this.end = LocalDateTime.parse(end, FORMATTER);
    }
}
