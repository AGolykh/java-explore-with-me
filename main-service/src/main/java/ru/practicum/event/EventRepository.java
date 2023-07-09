package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.Category;
import ru.practicum.event.dto.EventSearchParamsPublicDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e " +
            "where (e.initiator.id in :users OR :users = null) " +
            "and (e.state in :states OR :states = null) " +
            "and (e.category.id in :categories OR :categories = null) " +
            "and (cast(:rangeStart as date) != null and cast(:rangeStart as date) != null " +
            "and e.eventDate between cast(:rangeStart as date) and cast(:rangeEnd as date)) " +
            "or (cast(:rangeStart as date) = null and e.eventDate < cast(:rangeEnd as date)) " +
            "or (cast(:rangeEnd as date) = null and e.eventDate > cast(:rangeStart as date)) " +
            "or (cast(:rangeStart as date) = null and cast(:rangeStart as date) = null) ")
    List<Event> findEventsByParams(@Param("users") Set<Long> users,
                                   @Param("states") Set<State> states,
                                   @Param("categories") Set<Long> categories,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    @Query(" select e from Event e " +
            "where lower(e.annotation) like lower(concat('%', :text, '%')) " +
            "or lower(e.description) like lower(concat('%', :text, '%')) " +
            "or lower(e.title) like lower(concat('%', :text, '%'))" +
            "or :text = null " +
            "and e.category.id in :categories or :categories = null " +
            "and e.paid = :paid or :paid = null " +
            "and (cast(:rangeStart as date) != null and cast(:rangeStart as date) != null " +
            "and e.eventDate between cast(:rangeStart as date) and cast(:rangeEnd as date) ) " +
            "or (cast(:rangeStart as date) = null and e.eventDate < cast(:rangeEnd as date) )" +
            "or (cast(:rangeEnd as date) = null and e.eventDate > cast(:rangeStart as date) )" +
            "or (cast(:rangeStart as date) = null and cast(:rangeStart as date) = null) " +
            "and e.confirmedRequests < e.participantLimit or :onlyAvailable = null " +
            "and  e.state = 'PUBLISHED' " +
            "order by :sortType")
    List<Event> findEventsByParams(@Param("text") String text,
                                   @Param("categories") Set<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   @Param("onlyAvailable") Boolean onlyAvailable,
                                   @Param("sortType") EventSearchParamsPublicDto.SortType sort,
                                   Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    boolean existsByCategory(Category category);

    boolean existsByIdAndInitiatorId(Long id, Long userId);

    List<Event> findAllByIdIn(Set<Long> ids);

}
