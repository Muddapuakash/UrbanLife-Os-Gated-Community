package com.urbanlife.entity;

import java.time.LocalDateTime;

import com.urbanlife.enums.RegistrationStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "event_registrations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_event_user",
            columnNames = {
                "event_id",
                "user_id"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_registration_event",
            columnList = "event_id"
        ),
        @Index(
            name = "idx_registration_user",
            columnList = "user_id"
        )
    }
)
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "event_id",
        nullable = false
    )
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime registeredAt;

    public EventRegistration() {
    }

    @PrePersist
    public void onCreate() {

        registeredAt = LocalDateTime.now();

        if (status == null) {
            status = RegistrationStatus.REGISTERED;
        }
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(
            Long registrationId) {
        this.registrationId = registrationId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(
            RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}