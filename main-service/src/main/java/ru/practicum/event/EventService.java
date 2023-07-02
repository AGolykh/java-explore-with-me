package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryRepository;
import ru.practicum.event.dto.EventAdminUpdateRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearchParams;
import ru.practicum.event.model.EventSearchPublicParams;
import ru.practicum.event.model.State;
import ru.practicum.stats.StatsSender;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsSender statsSender;

    public List<EventFullDto> getAll(EventSearchParams eventSearchParams, HttpServletRequest httpServletRequest) {
        List<EventFullDto> result = eventRepository.findEventsByParams(
                        eventSearchParams.getUsers(),
                        eventSearchParams.getStates(),
                        eventSearchParams.getCategories(),
                        eventSearchParams.getRangeStart(),
                        eventSearchParams.getRangeEnd(),
                        getPage(eventSearchParams.getFrom(),
                                eventSearchParams.getSize()))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    public EventFullDto getById(Long eventId, HttpServletRequest httpServletRequest) {
        EventFullDto result = eventRepository
                .findById(eventId)
                .map(eventMapper::toEventFullDto)
                .orElseThrow(() -> new NullPointerException(String.format("Event %d is not found.", eventId)));

        log.info("Event {} is found.", result.getId());
        statsSender.send(httpServletRequest);
        return result;
    }

    public List<EventFullDto> getAll(EventSearchPublicParams eventSearchPublicParams,
                                      HttpServletRequest httpServletRequest) {

        List<EventFullDto> result = eventRepository
                .findEventsByParams(eventSearchPublicParams.getText(),
                        eventSearchPublicParams.getCategories(),
                        eventSearchPublicParams.getPaid(),
                        eventSearchPublicParams.getRangeStart(),
                        eventSearchPublicParams.getRangeEnd(),
                        eventSearchPublicParams.getOnlyAvailable(),
                        getPage(eventSearchPublicParams.getFrom(),
                        eventSearchPublicParams.getSize()))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    @Transactional
    public EventFullDto update(Long eventId, EventAdminUpdateRequestDto eventAdminUpdateRequestDto) {
        Event oldEvent = getEventById(eventId);
        Event newEvent = eventMapper.toEvent(eventAdminUpdateRequestDto);
        newEvent.setCategory(Optional.of(categoryRepository
                        .findById(eventAdminUpdateRequestDto.getCategory()))
                .get()
                .orElseThrow());

        if (!oldEvent.getState().equals(State.PENDING)) {
            throw new IllegalStateException("Wrong state of event: " +
                    oldEvent.getState());
        }

        if (eventAdminUpdateRequestDto.getStateAction()
                .equals(EventAdminUpdateRequestDto.StateAction.PUBLISH_EVENT)) {
            newEvent.setState(State.PUBLISHED);
        }
        if (eventAdminUpdateRequestDto.getStateAction()
                .equals(EventAdminUpdateRequestDto.StateAction.REJECT_EVENT)) {
            newEvent.setState(State.CANCELED);
        }

        EventFullDto result = Optional.of(eventRepository.save(newEvent))
                .map(eventMapper::toEventFullDto)
                .orElseThrow();

        log.info("Update event: {}", result.getTitle());
        return result;
    }

    public Event getEventById(Long eventId) {
        Event result = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NullPointerException(String.format("Event %d is not found.", eventId)));
        log.info("Event {} is found.", result.getId());
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
