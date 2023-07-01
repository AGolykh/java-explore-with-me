package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.stats.StatsSender;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StatsSender statsSender;

    public List<CategoryDto> getAll(Integer from, Integer size, HttpServletRequest request) {
        List<CategoryDto> result = categoryRepository.findAll(getPage(from, size))
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        log.info("Found {} category(ies).", result.size());
        statsSender.send(request);
        return result;
    }

    public CategoryDto getById(Long catId, HttpServletRequest request) {
        CategoryDto result = categoryRepository
                .findById(catId)
                .map(categoryMapper::toCategoryDto)
                .orElseThrow(() -> new NullPointerException(String.format("Category %d is not found.", catId)));

        log.info("Category {} is found.", result.getId());
        statsSender.send(request);
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
