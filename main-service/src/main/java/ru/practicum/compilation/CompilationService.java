package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> result = getCompilationsByParams(pinned, getPage(from, size))
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
        log.info("Found {} compilation(s).", result.size());
        return result;
    }

    public CompilationDto getById(Long compId) {
        CompilationDto result = compilationRepository
                .findById(compId)
                .map(compilationMapper::toCompilationDto)
                .orElseThrow(() -> new NullPointerException(String.format("Compilation %d is not found.", compId)));
        log.info("Compilation {} is found.", result.getId());
        return result;
    }

    @Transactional
    public CompilationDto create(CompilationNewDto compilationNewDto) {
        CompilationDto result = Optional.of(compilationRepository.save(compilationMapper.toCompilation(compilationNewDto)))
                .map(compilationMapper::toCompilationDto)
                .orElseThrow();

        log.info("Compilation {} {} created.", result.getId(), result.getTitle());
        return result;
    }

    @Transactional
    public void deleteById(Long compId) {
        Compilation result = getCompilationById(compId);
        compilationRepository.deleteById(result.getId());

        log.info("Compilation {} removed.", result.getTitle());
    }

    @Transactional
    public CompilationDto update(Long compId, CompilationNewDto compilationNewDto) {
        Compilation oldCompilation = getCompilationById(compId);
        compilationMapper.updateCompilation(oldCompilation, compilationNewDto);
        CompilationDto result = Optional.of(compilationRepository.save(oldCompilation))
                .map(compilationMapper::toCompilationDto)
                .orElseThrow();

        log.info("Compilation category: {}", result.getTitle());
        return result;
    }



    private List<Compilation> getCompilationsByParams(Boolean pinned, Pageable pageable) {
        return pinned == null ?
                compilationRepository.findAll(pageable).toList() :
                compilationRepository.findAllByPinned(pinned, pageable);
    }

    public Compilation getCompilationById(Long compId) {
        Compilation result = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NullPointerException(String.format("Compilation %d is not found.", compId)));
        log.info("Compilation {} is found.", result.getId());
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
