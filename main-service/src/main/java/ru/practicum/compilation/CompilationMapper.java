package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.EventMapper;
import ru.practicum.event.LocationMapper;
import ru.practicum.user.UserMapper;

@Component
@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, EventMapper.class, LocationMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);

    Compilation toCompilation(CompilationDto compilationDto);

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(CompilationNewDto compilationNewDto);

    @Mapping(target = "id", ignore = true)
    void updateCompilation(@MappingTarget Compilation compilation, CompilationNewDto compilationNewDto);
}
