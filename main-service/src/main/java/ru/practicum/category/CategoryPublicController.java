package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    public final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        return categoryService.getAll(from, size, request);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Long catId, HttpServletRequest request) {
        return categoryService.getById(catId, request);
    }
}
