package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

@Component
@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}
