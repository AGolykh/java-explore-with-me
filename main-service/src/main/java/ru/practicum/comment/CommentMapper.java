package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "event.id", target = "eventId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "event.id", target = "eventId")
    List<CommentDto> toCommentDto(List<Comment> comments);

    Comment toComment(CommentNewDto commentNewDto);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    void updateComment(@MappingTarget Comment comment, CommentNewDto commentNewDto);
}
