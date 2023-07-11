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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.comment.dto.CommentDto;
import ru.practicum.event.comment.dto.CommentSearchParamsDto;
import ru.practicum.exception.ApiError;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@Tag(name = "Admin: Комментарии", description = "Админский API для работы с комментариями")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @Operation(summary = "Удаление комментариев по запросу от администратора",
            tags = "Admin: Комментарии",
            description = "При удалении владельца комментария, удалятся так же и комментарий.\n" +
                    "\nПри удалении эвента, удалятся все комментарии к этому эвенту.",
            operationId = "deleteCommentByAdminRequest")
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
                    })
    })
    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Parameter(description = "id комментария") Long commentId,
                       HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        commentService.deleteByAdminRequest(commentId);
    }

    @Operation(summary = "Поиск комментариев по параметрам",
            tags = "Admin: Комментарии",
            description = "Эндпоинт возвращает полную информацию обо всех комментариях, подходящих под переданные условия.\n" +
                    "\nВ случае, если по заданным фильтрам не найдено ни одного комментария, возвращает пустой список",
            operationId = "getAllCommentsByParams")
    @GetMapping()
    public List<CommentDto> getAllByParams(
            @RequestParam(required = false)
            @Parameter(description = "Текст, присутствующий в комментарии") String text,
            @RequestParam(required = false)
            @Parameter(description = "Список id пользователей, чьи комментарии нужно найти") Set<Long> users,
            @RequestParam(required = false)
            @Parameter(description = "Список id эвентов, в которых будет вестись поиск") Set<Long> events,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @Parameter(description = "Дата и время не раньше которых должен быть опубликован комментарий") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @Parameter(description = "Дата и время не позже которых должен быть опубликован комментарий") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0")
            @Parameter(description = "Количество комментариев, которые нужно пропустить для формирования текущего набора") Integer from,
            @RequestParam(required = false, defaultValue = "10")
            @Parameter(description = "Количество комментариев в наборе") Integer size,
            HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentService.getAllByParams(new CommentSearchParamsDto(text,
                users,
                events,
                rangeStart,
                rangeEnd,
                from,
                size));
    }
}
