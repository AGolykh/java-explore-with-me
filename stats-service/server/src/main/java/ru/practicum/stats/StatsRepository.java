package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stats.model.ViewStats(eph.app, eph.uri, count(eph.ip)) " +
            "from EndpointHit eph " +
            "where eph.timestamp between ?1 and ?2 " +
            "and (eph.uri in ?3 OR ?3 = null) " +
            "group by eph.app, eph.uri " +
            "order by count(eph.ip) desc")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stats.model.ViewStats(eph.app, eph.uri, count(distinct eph.ip)) " +
            "from EndpointHit eph " +
            "where eph.timestamp between ?1 and ?2 " +
            "and (eph.uri in ?3 OR ?3 = null) " +
            "group by eph.app, eph.uri " +
            "order by count(distinct eph.ip) desc")
    List<ViewStats> getUniqueIPStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
