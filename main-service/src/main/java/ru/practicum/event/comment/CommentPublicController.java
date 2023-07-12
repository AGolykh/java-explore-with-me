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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.comment.dto.CommentDto;
import ru.practicum.exception.ApiError;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Validated
@Tag(name = "Public: Комментарии", description = "Публичный API для работы с комментариями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {
    private final CommentService commentService;


    @Operation(summary = "Просмотр комментариев к событию", tags = "Public: Комментарии",
            description = "Эндпоинт возвращает полную информацию обо всех комментариях к указанному событию\n" +
                    "\nВ случае, если по заданным фильтрам не найдено ни одного комментария, возвращает пустой список.",
            operationId = "getAllCommentsByEventId")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class))
                    })
    })
    @GetMapping("/{eventId}")
    public List<CommentDto> getAllByEventId(
            @PathVariable
            @Parameter(description = "id события") Long eventId,
            @RequestParam(required = false, defaultValue = "0")
            @Parameter(description = "Количество комментариев, которые нужно пропустить для формирования текущего набора") Integer from,
            @RequestParam(required = false, defaultValue = "10")
            @Parameter(description = "Количество комментариев в наборе") Integer size,
            HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return commentService.getAllByEventId(eventId, from, size);
    }
}
