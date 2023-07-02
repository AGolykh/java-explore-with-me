package ru.practicum.category;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(Long id);

    Category toCategory(CategoryDto categoryDto);

    Category toCategory(CategoryNewDto categoryNewDto);

    @Mapping(target = "id", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryNewDto categoryNewDto);
}
