package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStats toViewStats(ViewStatsDto viewStatsDto);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}
