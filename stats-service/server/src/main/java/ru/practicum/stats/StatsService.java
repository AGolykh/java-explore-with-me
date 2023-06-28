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
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setTimestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), DateRange.FORMATTER));

        EndpointHitDto result = Optional
                .of(statsRepository.save(EndpointHitMapper.mapToEndpointHit(endpointHitDto, endpointHit)))
                .map(EndpointHitMapper::mapToDto)
                .orElseThrow();

        log.info("EndpointHit {} added.", endpointHitDto);
        return result;
    }

    public List<ViewStatsDto> get(DateRange dateRange, List<String> uris, Boolean unique) {
        List<ViewStatsDto> result = (unique ?
                statsRepository.getUniqueIPStats(dateRange.getStart(), dateRange.getEnd(), uris) :
                statsRepository.getStats(dateRange.getStart(), dateRange.getEnd(), uris))
                .stream()
                .map(ViewStatsMapper::mapToDto)
                .collect(Collectors.toList());

        log.info("Found {} endpoint hits.", result.size());
        return result;
    }
}