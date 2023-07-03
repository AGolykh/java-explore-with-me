package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stats.model.ViewStats(eph.app, eph.uri, count(eph.ip)) " +
            "from EndpointHit eph " +
            "where eph.timestamp between cast(:start as date) and cast(:end as date) " +
            "and (eph.uri in :uris OR :uris = null) " +
            "group by eph.app, eph.uri " +
            "order by count(eph.ip) desc")
    List<ViewStats> getStats(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("uris") List<String> uris);

    @Query("select new ru.practicum.stats.model.ViewStats(eph.app, eph.uri, count(distinct eph.ip)) " +
            "from EndpointHit eph " +
            "where eph.timestamp between cast(:start as date) and cast(:end as date) " +
            "and (eph.uri in :uris OR :uris = null) " +
            "group by eph.app, eph.uri " +
            "order by count(distinct eph.ip) desc")
    List<ViewStats> getUniqueIPStats(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);
}
