package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.event.EventMapper;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.UserMapper;

import java.util.List;

@Component
@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, EventMapper.class, LocationMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toCompilationDto(List<Compilation> compilations);

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(CompilationNewDto compilationNewDto);

    void updateCompilation(@MappingTarget Compilation compilation, CompilationUpdateDto compilationUpdateDto);
}
