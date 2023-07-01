package ru.practicum.event;

import org.mapstruct.Mapper;
import ru.practicum.event.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);
}
