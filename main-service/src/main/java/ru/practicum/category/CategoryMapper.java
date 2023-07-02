package ru.practicum.category;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(Long id);

    Category toCategory(CategoryDto categoryDto);

    Category toCategory(CategoryNewDto categoryNewDto);

    @Mapping(target = "id", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryNewDto categoryNewDto);
}
