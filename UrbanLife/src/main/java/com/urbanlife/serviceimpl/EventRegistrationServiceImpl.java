package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.EventRegistrationResponse;
import com.urbanlife.entity.Event;
import com.urbanlife.entity.EventRegistration;
import com.urbanlife.entity.User;
import com.urbanlife.enums.EventStatus;
import com.urbanlife.enums.RegistrationStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.EventRegistrationRepository;
import com.urbanlife.repository.EventRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.EventRegistrationService;

@Service
@Transactional
public class EventRegistrationServiceImpl
        implements EventRegistrationService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public EventRegistrationServiceImpl(
            EventRepository eventRepository,
            EventRegistrationRepository registrationRepository,
            UserRepository userRepository) {

        this.eventRepository = eventRepository;
        this.registrationRepository =
                registrationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventRegistrationResponse register(
            Long eventId,
            Long userId) {

        Event event = findEvent(eventId);
        User user = findUser(userId);

        if (event.getStatus()
                != EventStatus.PUBLISHED) {

            throw new IllegalStateException(
                "Registration is allowed only for published events");
        }

        if (!event.getStartTime()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalStateException(
                "Event has already started");
        }

        if (event.getRegistrationDeadline()
                != null
                && LocalDateTime.now()
                    .isAfter(
                        event.getRegistrationDeadline())) {

            throw new IllegalStateException(
                "Registration deadline has passed");
        }

        if (registrationRepository
                .existsByEventEventIdAndUserUserId(
                    eventId,
                    userId)) {

            throw new IllegalStateException(
                "User is already registered for this event");
        }

        if (event.getMaxParticipants() != null) {

            long registrations =
                    registrationRepository
                        .countByEventEventIdAndStatus(
                            eventId,
                            RegistrationStatus.REGISTERED);

            if (registrations
                    >= event.getMaxParticipants()) {

                throw new IllegalStateException(
                    "Event registration is full");
            }
        }

        EventRegistration registration =
                new EventRegistration();

        registration.setEvent(event);
        registration.setUser(user);
        registration.setStatus(
                RegistrationStatus.REGISTERED);

        return mapToResponse(
                registrationRepository
                    .save(registration));
    }

    @Override
    public EventRegistrationResponse
            cancelRegistration(
                Long eventId,
                Long userId) {

        EventRegistration registration =
                findRegistration(
                    eventId,
                    userId);

        if (registration.getStatus()
                != RegistrationStatus.REGISTERED) {

            throw new IllegalStateException(
                "Only active registration can be cancelled");
        }

        registration.setStatus(
                RegistrationStatus.CANCELLED);

        return mapToResponse(
                registrationRepository
                    .save(registration));
    }

    @Override
    public EventRegistrationResponse markAttendance(
            Long eventId,
            Long userId) {

        EventRegistration registration =
                findRegistration(
                    eventId,
                    userId);

        if (registration.getStatus()
                != RegistrationStatus.REGISTERED) {

            throw new IllegalStateException(
                "Only registered users can be marked as attended");
        }

        registration.setStatus(
                RegistrationStatus.ATTENDED);

        return mapToResponse(
                registrationRepository
                    .save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRegistrationResponse>
            getEventRegistrations(
                Long eventId) {

        findEvent(eventId);

        return registrationRepository
                .findByEventEventIdOrderByRegisteredAtAsc(
                    eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRegistrationResponse>
            getUserRegistrations(
                Long userId) {

        findUser(userId);

        return registrationRepository
                .findByUserUserIdOrderByRegisteredAtDesc(
                    userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Event findEvent(Long eventId) {

        return eventRepository
                .findById(eventId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Event not found with id: "
                        + eventId));
    }

    private User findUser(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    private EventRegistration findRegistration(
            Long eventId,
            Long userId) {

        return registrationRepository
                .findByEventEventIdAndUserUserId(
                    eventId,
                    userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Event registration not found"));
    }

    private EventRegistrationResponse mapToResponse(
            EventRegistration registration) {

        EventRegistrationResponse response =
                new EventRegistrationResponse();

        response.setRegistrationId(
                registration.getRegistrationId());

        response.setEventId(
                registration.getEvent()
                    .getEventId());

        response.setEventTitle(
                registration.getEvent()
                    .getTitle());

        response.setUserId(
                registration.getUser()
                    .getUserId());

        response.setStatus(
                registration.getStatus());

        response.setRegisteredAt(
                registration.getRegisteredAt());

        return response;
    }
}