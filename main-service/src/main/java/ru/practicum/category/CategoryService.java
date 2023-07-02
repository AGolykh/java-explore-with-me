package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAll(Integer from, Integer size) {
        List<CategoryDto> result = categoryRepository.findAll(getPage(from, size))
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        log.info("Found {} category(ies).", result.size());
        return result;
    }

    public CategoryDto getById(Long catId) {
        CategoryDto result = categoryRepository
                .findById(catId)
                .map(categoryMapper::toCategoryDto)
                .orElseThrow(() -> new NullPointerException(String.format("Category %d is not found.", catId)));

        log.info("Category {} is found.", result.getId());
        return result;
    }

    @Transactional
    public CategoryDto create(CategoryNewDto categoryNewDto) {
        CategoryDto result = Optional.of(categoryRepository.save(categoryMapper.toCategory(categoryNewDto)))
                .map(categoryMapper::toCategoryDto)
                .orElseThrow();

        log.info("Category {} {} created.", result.getId(), result.getName());
        return result;
    }

    @Transactional
    public void deleteById(Long catId) {
        Category result = getCategoryById(catId);
        if (eventRepository.existsByCategory(result)) {
            throw new IllegalArgumentException("The category is not empty");
        }
        categoryRepository.deleteById(result.getId());

        log.info("Category {} removed.", result.getName());
    }

    @Transactional
    public CategoryDto update(CategoryNewDto categoryNewDto, Long catId) {
        Category oldCategory = getCategoryById(catId);
        categoryMapper.updateCategory(oldCategory, categoryNewDto);
        CategoryDto result = Optional.of(categoryRepository.save(oldCategory))
                .map(categoryMapper::toCategoryDto)
                .orElseThrow();

        log.info("Update category: {}", result.getName());
        return result;
    }

    public Category getCategoryById(Long catId) {
        Category result = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NullPointerException(String.format("Category %d is not found.", catId)));
        log.info("Category {} is found.", result.getId());
        return result;
    }


    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
