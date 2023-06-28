package ru.practicum.stats.mapper;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

public class ViewStatsMapper {
    public static ViewStatsDto mapToDto(ViewStats viewStats) {
        return new ViewStatsDto(
                viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits());
    }

    public static ViewStats mapToViewStats(ViewStatsDto viewStatsDto, ViewStats viewStats) {
        viewStats.setApp(viewStatsDto.getApp());
        viewStats.setUri(viewStatsDto.getUri());
        viewStats.setHits(viewStatsDto.getHits());
        return viewStats;
    }
}
