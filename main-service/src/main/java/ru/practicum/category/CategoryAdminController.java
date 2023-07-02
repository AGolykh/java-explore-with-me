package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    public final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@RequestBody @Valid CategoryNewDto categoryNewDto) {
        return categoryService.create(categoryNewDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteById(@PathVariable Long catId) {
        categoryService.deleteById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody @Valid CategoryNewDto categoryNewDto) {
        return categoryService.update(categoryNewDto, catId);
    }
}