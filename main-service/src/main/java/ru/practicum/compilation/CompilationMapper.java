package ru.practicum.compilation;

import org.mapstruct.Mapper;
import ru.practicum.compilation.dto.CompilationDto;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);
}
