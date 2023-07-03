package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.UserMapper;

@Component
@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper  {

    Event toEvent(EventAdminUpdateRequestDto eventAdminUpdateRequestDto);

    Event toEvent(Long id);

    Event toEvent(EventUserUpdateRequestDto eventUserUpdateRequestDto);

    Event toEvent(EventUpdateRequestDto eventUpdateRequestDto);

    Event toEvent(EventNewDto eventNewDto);

    EventDto toEventDto(Event event);

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);
}
