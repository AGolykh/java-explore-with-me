package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.request.Status.CANCELED;
import static ru.practicum.request.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    public List<RequestDto> getByUserId(Long userId) {
        User user = userService.getUserById(userId);

        List<RequestDto> result = requestMapper
                .toRequestDto(requestRepository.findAllByRequesterId(user.getId()));

        log.info("Found {} event(s).", result.size());
        return result;
    }

    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new IllegalStateException(String
                    .format("User %d must not be equal to initiator", userId));
        }

        if (requestRepository.existsByEventIdAndRequesterId(event.getId(), user.getId())) {
            throw new IllegalStateException(String
                    .format("Request by user %d already exists in event %d ", userId, eventId));
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new IllegalStateException(String
                    .format("Event with id=%d is not published", eventId));
        }

        if (!event.getParticipantLimit().equals(0L) &&
                event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new IllegalStateException(String
                    .format("Event with id=%d has reached participant limit", eventId));
        }

        Request request = new Request(null, event, user, LocalDateTime.now(), Status.PENDING);

        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) {
            request.setStatus(CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        RequestDto result = Optional.of(requestRepository.save(request))
                .map(requestMapper::toRequestDto)
                .orElseThrow();

        log.info("Request {}  created.", result.getId());
        return result;
    }

    @Transactional
    public RequestDto update(Long userId, Long requestId) {
        Request request = requestRepository
                .findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Request %d by User", requestId), userId));
        request.setStatus(CANCELED);
        RequestDto result = Optional.of(requestRepository.save(request))
                .map(requestMapper::toRequestDto)
                .orElseThrow();
        log.info("Request {}  is canceled.", result.getId());
        return result;
    }
}
