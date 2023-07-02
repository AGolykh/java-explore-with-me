package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e " +
            "where (e.initiator.id in ?1 OR ?1 = null) " +
            "and (e.state in ?2 OR ?2 = null) " +
            "and (e.category.id in ?3 OR ?3 = null) " +
            "and e.eventDate between ?4 and ?5")
    List<Event> findEventsByParams(
            Set<Long> users,
            Set<State> states,
            Set<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("select a from Event a " +
            "where a.state = 'PUBLISHED' " +
            "and (:#{#categories == null} = true " +
            "or a.category.id in :categories) " +
            "and (:#{#texts == null} = true " +
            "or (lower(a.annotation) like concat('%', :texts, '%') " +
            "or lower(a.description) like concat('%', :texts, '%'))) " +
            "and (:#{#paid == null} = true or a.paid = :paid) " +
            "and ((:#{#rangeStart != null} = true " +
            "and :#{#rangeEnd != null} = true " +
            "and a.eventDate between :rangeStart and :rangeEnd) " +
            "or ((:#{#rangeStart == null} = true " +
            "or a.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true " +
            "or a.eventDate <= :rangeEnd))) " +
            "and (:#{#onlyAvailable == false} = true " +
            "or a.confirmedRequests < a.participantLimit)")
    Page<Event> findEventsByParams(@Param("texts") String texts,
                                   @Param("categories") Set<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   @Param("onlyAvailable") Boolean onlyAvailable,
                                   Pageable page);

    boolean existsByCategory(Category category);
}
