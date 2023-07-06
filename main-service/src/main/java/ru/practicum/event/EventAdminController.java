package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventAdminUpdateRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventSearchParamsAdminDto;
import ru.practicum.event.model.State;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class EventAdminController {
    public final EventService eventService;

    @GetMapping()
    public List<EventFullDto> getAll(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<State> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.getAllByParams(new EventSearchParamsAdminDto(users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        from,
                        size),
                httpServletRequest);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateAdmin(@PathVariable Long eventId,
                                    @RequestBody @Valid EventAdminUpdateRequestDto eventAdminUpdateRequestDto,
                                    HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.updateByAdminRequest(eventId, eventAdminUpdateRequestDto);
    }
}
