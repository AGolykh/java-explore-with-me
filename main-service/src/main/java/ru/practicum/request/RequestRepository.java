package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester_Id(Long userId);

    Optional<Request> findByIdAndRequester_Id(Long requestId, Long userId);

    List<Request> findAllByEvent_Id(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEvent_IdAndStatus(Long eventId, Status status);

}
