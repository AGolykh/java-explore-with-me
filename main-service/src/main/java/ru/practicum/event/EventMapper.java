package ru.practicum.event;

import org.mapstruct.Mapper;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto eventToEventDto(Event event);

    EventShortDto eventToEventShortDto(Event event);

    EventFullDto eventToEventFullDto(Event event);
}
