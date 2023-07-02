package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    boolean existsByCategory(Category category);
}
