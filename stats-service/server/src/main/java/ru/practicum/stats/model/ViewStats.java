package ru.practicum.stats.model;

import lombok.*;

@Data
@AllArgsConstructor
public class ViewStats {

    private String app;
    private String uri;
    private Long hits;
}
