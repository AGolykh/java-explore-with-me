package ru.practicum.event;

import lombok.*;
import ru.practicum.category.Category;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EVENTS", schema = "PUBLIC")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "CONFIRMED_REQUESTS")
    private long confirmedRequests;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;

    @OneToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    private Boolean paid;

    @Column(name = "PARTICIPANT_LIMIT")
    private Long participantLimit;

    @Column(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;

    @Column(name = "REQUEST_MODERATION")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    private Long views;
}
