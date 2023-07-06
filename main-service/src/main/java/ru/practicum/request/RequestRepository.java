package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventIdAndStatus(Long eventId, Status status);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

}
