package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
