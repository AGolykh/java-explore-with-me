package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.mapper.ViewStatsMapper;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateDate(start, end);
        List<ViewStatsDto> result = viewStatsMapper
                .toViewStatsDto(getViewStatsByParams(start, end, uris, unique));

        log.info("Found {} endpoint hits.", result.size());
        return result;
    }

    private List<ViewStats> getViewStatsByParams(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return unique ?
                statsRepository.getUniqueIPStats(start, end, uris) :
                statsRepository.getStats(start, end, uris);
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (!(start.isBefore(end) && !start.equals(end))) {
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }
    }
}