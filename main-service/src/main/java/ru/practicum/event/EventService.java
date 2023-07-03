package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.CategoryService;
import ru.practicum.event.dto.EventAdminUpdateRequestDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUserUpdateRequestDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearchParams;
import ru.practicum.event.model.EventSearchPublicParams;
import ru.practicum.event.model.State;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdatedDto;
import ru.practicum.request.dto.RequestsStatusUpdateDto;
import ru.practicum.stats.StatsSender;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final StatsSender statsSender;

    public List<EventFullDto> getAll(EventSearchParams eventSearchParams,
                                     HttpServletRequest httpServletRequest) {
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

    public List<EventFullDto> getAll(EventSearchPublicParams eventSearchPublicParams,
                                     HttpServletRequest httpServletRequest) {
        List<EventFullDto> result = eventRepository
                .findEventsByParams(eventSearchPublicParams.getText(),
                        eventSearchPublicParams.getCategories(),
                        eventSearchPublicParams.getPaid(),
                        eventSearchPublicParams.getRangeStart(),
                        eventSearchPublicParams.getRangeEnd(),
                        eventSearchPublicParams.getOnlyAvailable(),
                        eventSearchPublicParams.getSort(),
                        getPage(eventSearchPublicParams.getFrom(),
                                eventSearchPublicParams.getSize()))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    public List<EventFullDto> getAll(Long userId, Integer from, Integer size,
                                     HttpServletRequest httpServletRequest) {
        User user = userService.getUserById(userId);
        List<EventFullDto> result = eventRepository.findAllByInitiator_Id(user.getId(), getPage(from, size))
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    @Transactional
    public EventFullDto create(Long userId, EventNewDto eventNewDto) {
        Event event = makeNewEvent(userId, eventNewDto);
        EventFullDto result = Optional.of(eventRepository.save(event))
                .map(eventMapper::toEventFullDto)
                .orElseThrow();

        log.info("Add event: {}", result.getTitle());
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

    @Transactional
    public EventFullDto update(Long userId, Long eventId,
                               EventUserUpdateRequestDto eventUserUpdateRequestDto) {
        return null;
    }

    @Transactional
    public RequestUpdatedDto updateRequestsStatus(Long userId, Long eventId,
                                                  RequestsStatusUpdateDto requestsStatusUpdateDto) {
        return null;
    }

    public EventFullDto getEventByUserEventId(Long userId, Long eventId, HttpServletRequest httpServletRequest) {
        User user = userService.getUserById(userId);
        EventFullDto result = eventRepository.findByInitiator_IdAndId(user.getId(), eventId)
                .map(eventMapper::toEventFullDto)
                .orElseThrow();

        log.info("Found event {}.", result.getId());
        statsSender.send(httpServletRequest);
        return result;
    }

    public List<RequestDto> getRequestsByUserEventId(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new IllegalArgumentException(
                    String.format("Event not found with id = %s and userId = %s", eventId, userId));
        }


        List<RequestDto> result = requestRepository.findAllByEvent_Id(eventId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} request(s).", result.size());
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


    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }

    private Event makeNewEvent(Long userId, EventNewDto eventNewDto) {
        validateDate(eventNewDto.getEventDate());
        User user = userService.getUserById(userId);
        Event event = eventMapper.toEvent(eventNewDto);
        Category category = categoryService.getCategoryById(eventNewDto.getCategory());
        Location location = locationMapper.toLocation(eventNewDto.getLocation());
        location = locationRepository.existsByLatAndLon(location.getLat(), location.getLon())
                ? locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                : locationRepository.save(location);

        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0L);
        event.setState(State.PENDING);
        event.setViews(0L);
        event.setRequestModeration(true);
        return event;
    }

    public Event getEventById(Long eventId) {
        Event result = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NullPointerException(String.format("Event %d is not found.", eventId)));
        log.info("Event {} is found.", result.getId());
        return result;
    }

    private void validateDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("There must be more than 2 hours before the event");
        }
    }
}
