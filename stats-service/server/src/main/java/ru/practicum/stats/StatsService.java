package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.mapper.ViewStatsMapper;
import ru.practicum.stats.model.DateRange;
import ru.practicum.stats.model.ViewStats;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;
    private final ViewStatsMapper viewStatsMapper;
    private final EndpointHitMapper endpointHitMapper;

    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHitDto result = Optional
                .of(statsRepository.save(endpointHitMapper.toEndpointHit(endpointHitDto)))
                .map(endpointHitMapper::toEndpointHitDto)
                .orElseThrow();

        log.info("EndpointHit {} added.", endpointHitDto);
        return result;
    }

    public List<ViewStatsDto> get(DateRange dateRange, List<String> uris, Boolean unique) {
        List<ViewStatsDto> result = getViewStatsByParams(dateRange, uris, unique)
                .stream()
                .map(viewStatsMapper::toViewStatsDto)
                .collect(Collectors.toList());

        log.info("Found {} endpoint hits.", result.size());
        return result;
    }

    private List<ViewStats> getViewStatsByParams(DateRange dateRange, List<String> uris, Boolean unique) {
        return unique ?
                statsRepository.getUniqueIPStats(dateRange.getStart(), dateRange.getEnd(), uris) :
                statsRepository.getStats(dateRange.getStart(), dateRange.getEnd(), uris);
    }
}