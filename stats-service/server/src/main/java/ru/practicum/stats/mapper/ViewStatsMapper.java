package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto toViewStatsDto(ViewStats viewStats);

    List<ViewStatsDto> toViewStatsDto(List<ViewStats> viewStats);
}
