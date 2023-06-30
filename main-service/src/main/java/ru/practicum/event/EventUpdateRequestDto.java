package ru.practicum.event;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class EventUpdateRequestDto {

    @Size(min = 1, max = 500)
    private String annotation;
    private Long category;
    @Size(min = 1, max = 2000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 1, max = 120)
    private String title;
}

