package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<RequestDto> result = requestRepository.findAllByRequester_Id(user.getId())
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} event(s).", result.size());
        return result;
    }

    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new IllegalArgumentException(String
                    .format("User with id=%d must not be equal to initiator", userId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException(String
                    .format("Event with id=%d is not published", eventId));
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new IllegalArgumentException(String
                    .format("Event with id=%d has reached participant limit", eventId));
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        Request request = new Request(null, event, user, LocalDateTime.now(), Status.PENDING);

        RequestDto result = Optional.of(requestRepository.save(request))
                .map(requestMapper::toRequestDto)
                .orElseThrow();

        log.info("Request {}  created.", result.getId());
        return result;
    }

    @Transactional
    public RequestDto update(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new NullPointerException(String.format("Request with id=%d " +
                        "and requesterId=%d was not found", requestId, userId)));
        request.setStatus(Status.CANCELED);
        RequestDto result = Optional.of(requestRepository.save(request))
                .map(requestMapper::toRequestDto)
                .orElseThrow();
        log.info("Request {}  is canceled.", result.getId());
        return result;
    }
}
