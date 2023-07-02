package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventAdminUpdateRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventSearchParams;
import ru.practicum.event.model.State;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class EventAdminController {
    public final EventService eventService;

    @GetMapping()
    public List<EventFullDto> getAll(@RequestParam(required = false) Set<Long> users,
                                     @RequestParam(required = false) Set<State> states,
                                     @RequestParam(required = false) Set<Long> categories,
                                     @RequestParam(required = false) String rangeStart,
                                     @RequestParam(required = false) String rangeEnd,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     HttpServletRequest httpServletRequest) {
        return eventService.getAll(new EventSearchParams(users, states, categories, rangeStart, rangeEnd, from, size),
                httpServletRequest);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody EventAdminUpdateRequestDto eventAdminUpdateRequestDto) {
        return eventService.update(eventId, eventAdminUpdateRequestDto);
    }
}
