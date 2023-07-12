package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.Request;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.Status;
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

import static ru.practicum.event.dto.EventAdminUpdateRequestDto.StateAction.PUBLISH_EVENT;
import static ru.practicum.event.dto.EventAdminUpdateRequestDto.StateAction.REJECT_EVENT;
import static ru.practicum.event.model.State.*;
import static ru.practicum.request.Status.CONFIRMED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final StatsSender statsSender;

    @Transactional
    public List<EventFullDto> getAllByParams(EventSearchParamsAdminDto eventSearchParamsAdminDto,
                                             HttpServletRequest httpServletRequest) {
        if ((eventSearchParamsAdminDto.getRangeStart() != null)
                && (eventSearchParamsAdminDto.getRangeEnd() != null)) {
            validateDate(eventSearchParamsAdminDto.getRangeStart(), eventSearchParamsAdminDto.getRangeEnd());
        }
        List<Event> events = eventRepository.findEventsByParams(
                eventSearchParamsAdminDto.getUsers(),
                eventSearchParamsAdminDto.getStates(),
                eventSearchParamsAdminDto.getCategories(),
                eventSearchParamsAdminDto.getRangeStart(),
                eventSearchParamsAdminDto.getRangeEnd(),
                getPage(eventSearchParamsAdminDto.getFrom(),
                        eventSearchParamsAdminDto.getSize()));

        addViews(events);

        List<EventFullDto> result = eventMapper.toEventFullDto(events);

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    @Transactional
    public List<EventFullDto> getAllByParamsAndPublished(EventSearchParamsPublicDto eventSearchParamsPublicDto,
                                                         HttpServletRequest httpServletRequest) {
        if ((eventSearchParamsPublicDto.getRangeStart() != null)
                && (eventSearchParamsPublicDto.getRangeEnd() != null)) {
            validateDate(eventSearchParamsPublicDto.getRangeStart(), eventSearchParamsPublicDto.getRangeEnd());
        }

        List<Event> events = eventRepository
                .findEventsByParams(eventSearchParamsPublicDto.getText(),
                        eventSearchParamsPublicDto.getCategories(),
                        eventSearchParamsPublicDto.getPaid(),
                        eventSearchParamsPublicDto.getRangeStart(),
                        eventSearchParamsPublicDto.getRangeEnd(),
                        eventSearchParamsPublicDto.getOnlyAvailable(),
                        eventSearchParamsPublicDto.getSort(),
                        getPage(eventSearchParamsPublicDto.getFrom(),
                                eventSearchParamsPublicDto.getSize()));

        addViews(events);

        List<EventFullDto> result = eventMapper.toEventFullDto(events);

        log.info("Found {} event(s).", result.size());
        statsSender.send(httpServletRequest);
        return result;
    }

    @Transactional
    public List<EventFullDto> getAllByUserId(Long userId, Integer from, Integer size,
                                             HttpServletRequest httpServletRequest) {
        User user = userService.getUserById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(user.getId(), getPage(from, size));

        addViews(events);

        List<EventFullDto> result = eventMapper.toEventFullDto(events);

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
    public EventFullDto updateByAdminRequest(Long eventId, EventAdminUpdateRequestDto eventAdminUpdateRequestDto) {
        Event oldEvent = getEventById(eventId);
        eventMapper.updateEvent(oldEvent, eventAdminUpdateRequestDto);

        validateBeforeDate(oldEvent.getEventDate(), 1);

        if (eventAdminUpdateRequestDto.getStateAction() != null) {

            if (eventAdminUpdateRequestDto.getStateAction().equals(PUBLISH_EVENT)
                    && !oldEvent.getState().equals(PENDING)) {
                throw new IllegalStateException("Wrong state of event: " +
                        oldEvent.getState());
            }

            if ((eventAdminUpdateRequestDto.getStateAction().equals(REJECT_EVENT) ||
                    (eventAdminUpdateRequestDto).getStateAction().equals(PUBLISH_EVENT))
                    && oldEvent.getState().equals(PUBLISHED)) {
                throw new IllegalStateException("Wrong state of event: " +
                        oldEvent.getState());
            }

            switch (eventAdminUpdateRequestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    oldEvent.setState(PUBLISHED);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    oldEvent.setState(CANCELED);
                    break;
            }
        }

        makeUpdatedEvent(oldEvent, eventAdminUpdateRequestDto);

        EventFullDto result = Optional.of(eventRepository.save(oldEvent))
                .map(eventMapper::toEventFullDto)
                .orElseThrow();

        log.info("Update event: {}", result.getTitle());
        return result;
    }

    @Transactional
    public EventFullDto updateByUserRequest(Long userId, Long eventId,
                                            EventUserUpdateRequestDto eventUserUpdateRequestDto) {
        Event oldEvent = getEventById(eventId);

        eventMapper.updateEvent(oldEvent, eventUserUpdateRequestDto);
        User user = userService.getUserById(userId);

        validateBeforeDate(oldEvent.getEventDate(), 2);

        if (!oldEvent.getInitiator().getId().equals(user.getId())) {
            throw new IllegalStateException("User don't have event with id " + eventId);
        }

        if (oldEvent.getState().equals(PUBLISHED)) {
            throw new IllegalStateException("Wrong state of event: " +
                    oldEvent.getState());
        }

        if (eventUserUpdateRequestDto.getStateAction() != null) {
            switch (eventUserUpdateRequestDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setState(PENDING);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setState(CANCELED);
                    break;
            }
        }

        makeUpdatedEvent(oldEvent, eventUserUpdateRequestDto);

        EventFullDto result = Optional.of(eventRepository.save(oldEvent))
                .map(eventMapper::toEventFullDto)
                .orElseThrow();

        log.info("Update event: {}", result.getTitle());
        return result;
    }

    @Transactional
    public RequestUpdatedDto updateRequestsStatus(Long userId, Long eventId,
                                                  RequestsStatusUpdateDto requestsStatusUpdateDto) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new IllegalStateException("User don't have event with id " + eventId);
        }

        if (event.getConfirmedRequests() +
                requestsStatusUpdateDto.getRequestIds().size() > event.getParticipantLimit()) {
            throw new IllegalStateException("Limit ");
        }

        List<Request> updatedRequestStatus = requestRepository
                .findAllByIdIn(requestsStatusUpdateDto.getRequestIds());

        updatedRequestStatus.forEach(request -> {
            if (request.getStatus().equals(CONFIRMED)) {
                throw new IllegalStateException("Request already confirmed " + request.getId());
            }
        });

        updatedRequestStatus.forEach(request -> request.setStatus(requestsStatusUpdateDto.getStatus()));
        event.setConfirmedRequests(event.getConfirmedRequests() + updatedRequestStatus.size());

        requestRepository.saveAll(updatedRequestStatus);
        eventRepository.save(event);

        List<RequestDto> confirmedRequests = requestMapper
                .toRequestDto(requestRepository.findAllByEventIdAndStatus(eventId, CONFIRMED));

        List<RequestDto> rejectedRequests = requestMapper
                .toRequestDto(requestRepository.findAllByEventIdAndStatus(eventId, Status.REJECTED));

        return new RequestUpdatedDto(confirmedRequests, rejectedRequests);
    }

    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId,
                                                   HttpServletRequest httpServletRequest) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findByInitiatorIdAndId(user.getId(), eventId)
                .orElseThrow();

        addViews(List.of(event));

        EventFullDto result = eventMapper.toEventFullDto(event);

        log.info("Found event {}.", result.getId());
        statsSender.send(httpServletRequest);
        return result;
    }

    public List<RequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new IllegalStateException(
                    String.format("Event not found with id = %s and userId = %s", eventId, userId));
        }

        List<RequestDto> result = requestMapper
                .toRequestDto(requestRepository.findAllByEventId(eventId));

        log.info("Found {} request(s).", result.size());
        return result;
    }

    public EventFullDto getById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event", eventId);
        }

        addViews(List.of(event));

        EventFullDto result = eventMapper.toEventFullDto(event);

        log.info("Event {} is found.", result.getId());
        statsSender.send(httpServletRequest);
        return result;
    }

    public Event getEventById(Long eventId) {
        Event result = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event", eventId));
        log.info("Event {} is found.", result.getId());
        return result;
    }

    private void addViews(List<Event> events) {
        events.forEach(event -> event.setViews(+1L));
        eventRepository.saveAll(events);
    }

    private Event makeNewEvent(Long userId, EventNewDto eventNewDto) {
        validateBeforeDate(eventNewDto.getEventDate(), 2);
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
        event.setState(PENDING);
        event.setViews(0L);

        event.setPaid(eventNewDto.getPaid() != null ? eventNewDto.getPaid() : false);
        event.setParticipantLimit(eventNewDto.getParticipantLimit() != null ? event.getParticipantLimit() : 0L);
        event.setRequestModeration(eventNewDto.getRequestModeration() != null ? event.getRequestModeration() : true);

        return event;
    }

    private <T extends EventUpdateRequestDto> void makeUpdatedEvent(Event event,
                                                                    T eventUpdateRequestDto) {
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryService.getCategoryById(eventUpdateRequestDto.getCategory());
            event.setCategory(category);
        }

        if (eventUpdateRequestDto.getLocation() != null) {
            Location location = locationMapper.toLocation(eventUpdateRequestDto.getLocation());
            location = locationRepository.existsByLatAndLon(location.getLat(), location.getLon())
                    ? locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                    : locationRepository.save(location);
            event.setLocation(location);
        }
    }

    private void validateBeforeDate(LocalDateTime eventDate, Integer hours) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new IllegalArgumentException("There must be more than 2 hours before the event");
        }
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (!(start.isBefore(end) && !start.equals(end))) {
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }

}
