package ru.practicum.event.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    @Query(" select c from Comment c " +
            "where (lower(c.text) like lower(concat('%', :text, '%')) or :text = null) " +
            "and (c.author.id in :users or :users = null) " +
            "and (c.event.id in :events or :events = null) " +
            "and ((cast(:rangeStart as date) != null and cast(:rangeStart as date) != null " +
            "and c.created between cast(:rangeStart as date) and cast(:rangeEnd as date) ) " +
            "or (cast(:rangeStart as date) = null and c.created < cast(:rangeEnd as date) )" +
            "or (cast(:rangeEnd as date) = null and c.created > cast(:rangeStart as date) )" +
            "or (cast(:rangeStart as date) = null and cast(:rangeStart as date) = null))")
    List<Comment> findCommentsByParams(@Param("text") String text,
                                     @Param("users") Set<Long> users,
                                     @Param("events") Set<Long> events,
                                     @Param("rangeStart") LocalDateTime rangeStart,
                                     @Param("rangeEnd") LocalDateTime rangeEnd,
                                     Pageable pageable);
}
