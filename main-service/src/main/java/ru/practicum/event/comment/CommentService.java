package ru.practicum.event.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.comment.dto.CommentDto;
import ru.practicum.event.comment.dto.CommentNewDto;
import ru.practicum.event.comment.dto.CommentSearchParamsDto;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto create(Long userId, Long eventId, CommentNewDto commentNewDto) {
        Comment comment = commentMapper.toComment(commentNewDto);
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        CommentDto result = Optional.of(commentRepository.save(comment))
                .map(commentMapper::toCommentDto)
                .orElseThrow();

        log.info("Comment {} for event {} created.", result.getId(), result.getEventId());
        return result;
    }

    @Transactional
    public CommentDto update(Long userId, Long commentId, CommentNewDto commentNewDto) {
        User user = userService.getUserById(userId);
        Comment oldComment = getCommentById(commentId);

        if (!user.getId().equals(oldComment.getAuthor().getId())) {
            throw new IllegalStateException("User don't have comment with id " + oldComment.getId());
        }

        commentMapper.updateComment(oldComment, commentNewDto);
        oldComment.setCreated(LocalDateTime.now());

        CommentDto result = Optional.of(commentRepository.save(oldComment))
                .map(commentMapper::toCommentDto)
                .orElseThrow();

        log.info("Update comment: {}", result.getId());
        return result;
    }

    @Transactional
    public void deleteByUserRequest(Long userId, Long commentId) {
        User user = userService.getUserById(userId);
        Comment comment = getCommentById(commentId);

        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new IllegalStateException("User don't have comment with id " + comment.getId());
        }

        commentRepository.deleteById(commentId);

        log.info("Delete comment: {}", comment.getId());
    }

    @Transactional
    public void deleteByAdminRequest(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.deleteById(commentId);

        log.info("Delete comment: {}", comment.getId());
    }

    public List<CommentDto> getAllByParams(CommentSearchParamsDto commentSearchParamsDto) {
        if ((commentSearchParamsDto.getRangeStart() != null)
                && (commentSearchParamsDto.getRangeEnd() != null)) {
            validateDate(commentSearchParamsDto.getRangeStart(), commentSearchParamsDto.getRangeEnd());
        }

        List<CommentDto> result = commentMapper.toCommentDto(commentRepository.findCommentsByParams(
                commentSearchParamsDto.getText(),
                commentSearchParamsDto.getUsers(),
                commentSearchParamsDto.getEvents(),
                commentSearchParamsDto.getRangeStart(),
                commentSearchParamsDto.getRangeEnd(),
                getPage(commentSearchParamsDto.getFrom(),
                        commentSearchParamsDto.getSize())));

        log.info("Found {} comment(s).", result.size());
        return result;
    }

    public List<CommentDto> getAllByUserId(Long userId, Integer from, Integer size) {
        User user = userService.getUserById(userId);
        List<CommentDto> result = commentMapper.toCommentDto(commentRepository
                .findAllByAuthorId(user.getId(), getPage(from, size)));

        log.info("Found {} comment(s).", result.size());
        return result;
    }

    public List<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size) {
        Event event = eventService.getEventById(eventId);
        List<CommentDto> result = commentMapper.toCommentDto(commentRepository
                .findAllByEventId(event.getId(), getPage(from, size)));

        log.info("Found {} comment(s).", result.size());
        return result;
    }

    public Comment getCommentById(Long comId) {
        Comment result = commentRepository
                .findById(comId)
                .orElseThrow(() -> new NotFoundException("Comment", comId));
        log.info("Comment {} is found.", result.getId());
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (!(start.isBefore(end) && !start.equals(end))) {
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }
    }
}
