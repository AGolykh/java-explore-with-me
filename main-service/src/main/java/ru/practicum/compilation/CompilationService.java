package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.stats.StatsSender;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final StatsSender statsSender;

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size, HttpServletRequest request) {
        List<CompilationDto> result = getCompilationsByParams(pinned, getPage(from, size))
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        log.info("Found {} compilation(s).", result.size());
        statsSender.send(request);
        return result;
    }

    public CompilationDto getById(Long compId, HttpServletRequest request) {
        CompilationDto result = compilationRepository
                .findById(compId)
                .map(compilationMapper::toCompilationDto)
                .orElseThrow(() -> new NullPointerException(String.format("Compilation %d is not found.", compId)));
        log.info("Compilation {} is found.", result.getId());
        statsSender.send(request);
        return result;
    }

    private List<Compilation> getCompilationsByParams(Boolean pinned, Pageable pageable) {
        return pinned == null ?
                compilationRepository.findAll(pageable).toList() :
                compilationRepository.findAllByPinned(pinned, pageable);
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
