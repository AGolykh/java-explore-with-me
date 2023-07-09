package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUserUpdateRequestDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdatedDto;
import ru.practicum.request.dto.RequestsStatusUpdateDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    public final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAll(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.getAllByUserId(userId, from, size, httpServletRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid EventNewDto eventDto,
                               HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserEventId(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.getEventByUserIdAndEventId(userId, eventId, httpServletRequest);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody @Valid EventUserUpdateRequestDto eventUserUpdateRequestDto,
                               HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.updateByUserRequest(userId, eventId, eventUserUpdateRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByUserEventId(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdatedDto updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid RequestsStatusUpdateDto requestsStatusUpdateDto,
            HttpServletRequest httpServletRequest) {
        log.info("Получен {} запрос к {} от {} ", httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());
        return eventService.updateRequestsStatus(userId, eventId, requestsStatusUpdateDto);
    }
}
