package ru.practicum.event;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class LocationDto {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
