package ru.practicum.event.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.comment.dto.CommentDto;
import ru.practicum.event.comment.dto.CommentNewDto;
import ru.practicum.exception.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@Tag(name = "Private: Комментарии", description = "Закрытый API для работы с комментариями")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @Operation(summary = "Добавление нового комментария к событию",
            tags = "Private: Комментарии",
            operationId = "createComment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))
                    })
    })
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable
                             @Parameter(description = "id события") Long userId,
                             @PathVariable
                             @Parameter(description = "id события") Long eventId,
                             @RequestBody @Valid CommentNewDto commentNewDto,
                             HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentService.create(userId, eventId, commentNewDto);
    }

    @Operation(summary = "Обновление комментария",
            tags = "Private: Комментарии",
            operationId = "updateComment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Комментарий не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))
                    })
    })
    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable
                             @Parameter(description = "id пользователя") Long userId,
                             @PathVariable
                             @Parameter(description = "id комментария") Long commentId,
                             @RequestBody @Valid CommentNewDto commentNewDto,
                             HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentService.update(userId, commentId, commentNewDto);
    }

    @Operation(summary = "Удаление комментариев по запросу от пользователя",
            tags = "Private: Комментарии",
            operationId = "deleteCommentByUserRequest")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Комментарий удален"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Комментарий не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь не является автором комментария",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    })
    })
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable
                           @Parameter(description = "id пользователя") Long userId,
                           @PathVariable
                           @Parameter(description = "id комментария") Long commentId,
                           HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        commentService.deleteByUserRequest(userId, commentId);
    }

    @Operation(summary = "Получение комментариев пользователя",
            tags = "Private: Комментарии",
            description = "Эндпоинт возвращает полную информацию обо всех комментариях, оставленных пользователем.\n" +
                    "\nВ случае, если нет ни одного комментария, возвращает пустой список",
            operationId = "getAllCommentsByUserId")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))
                    })
    })
    @GetMapping()
    public List<CommentDto> getAllByUserId(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentService.getAllByUserId(userId, from, size);
    }
}
