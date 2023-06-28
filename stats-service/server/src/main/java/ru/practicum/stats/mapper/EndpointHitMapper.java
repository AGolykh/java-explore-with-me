package ru.practicum.stats.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

public class EndpointHitMapper {

    public static EndpointHitDto mapToDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp());
    }

    public static EndpointHit mapToEndpointHit(EndpointHitDto endpointHitDto, EndpointHit endpointHit) {
        endpointHit.setId(endpointHitDto.getId());
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        return endpointHit;
    }
}
