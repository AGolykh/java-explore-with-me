package ru.practicum.event;

import org.mapstruct.Mapper;
import ru.practicum.category.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.UserMapper;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class})
public interface EventMapper  {

    Event toEvent(EventAdminUpdateRequestDto eventAdminUpdateRequestDto);

    Event toEvent(Long id);

    Event toEvent(EventUserUpdateRequestDto eventUserUpdateRequestDto);

    Event toEvent(EventUpdateRequestDto eventUpdateRequestDto);

    EventDto toEventDto(Event event);

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);
}
