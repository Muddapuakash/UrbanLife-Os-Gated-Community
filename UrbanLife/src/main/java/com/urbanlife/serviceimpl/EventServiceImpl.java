package com.urbanlife.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.dto.EventRequest;
import com.urbanlife.dto.EventResponse;
import com.urbanlife.entity.Community;
import com.urbanlife.entity.Event;
import com.urbanlife.enums.EventStatus;
import com.urbanlife.enums.RegistrationStatus;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.CommunityRepository;
import com.urbanlife.repository.EventRegistrationRepository;
import com.urbanlife.repository.EventRepository;
import com.urbanlife.service.EventService;

@Service
@Transactional
public class EventServiceImpl
        implements EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final CommunityRepository communityRepository;

    public EventServiceImpl(
            EventRepository eventRepository,
            EventRegistrationRepository registrationRepository,
            CommunityRepository communityRepository) {

        this.eventRepository = eventRepository;
        this.registrationRepository =
                registrationRepository;
        this.communityRepository =
                communityRepository;
    }

    @Override
    public EventResponse createEvent(
            EventRequest request) {

        validateDates(request);

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        Event event = new Event();

        mapRequest(event, request);

        event.setCommunity(community);
        event.setStatus(EventStatus.DRAFT);

        return mapToResponse(
                eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(
            Long eventId) {

        return mapToResponse(
                findEvent(eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getCommunityEvents(
            Long communityId) {

        validateCommunity(communityId);

        return eventRepository
                .findByCommunityCommunityIdOrderByStartTimeAsc(
                    communityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByStatus(
            Long communityId,
            EventStatus status) {

        validateCommunity(communityId);

        return eventRepository
                .findByCommunityCommunityIdAndStatusOrderByStartTimeAsc(
                    communityId,
                    status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EventResponse updateEvent(
            Long eventId,
            EventRequest request) {

        validateDates(request);

        Event event = findEvent(eventId);

        if (event.getStatus()
                == EventStatus.COMPLETED) {

            throw new IllegalStateException(
                "Completed event cannot be updated");
        }

        if (event.getStatus()
                == EventStatus.CANCELLED) {

            throw new IllegalStateException(
                "Cancelled event cannot be updated");
        }

        Community community =
                communityRepository
                    .findById(request.getCommunityId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Community not found with id: "
                            + request.getCommunityId()));

        mapRequest(event, request);

        event.setCommunity(community);

        return mapToResponse(
                eventRepository.save(event));
    }

    @Override
    public EventResponse publishEvent(
            Long eventId) {

        Event event = findEvent(eventId);

        if (event.getStatus()
                != EventStatus.DRAFT) {

            throw new IllegalStateException(
                "Only draft events can be published");
        }

        if (!event.getStartTime()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalStateException(
                "Past event cannot be published");
        }

        event.setStatus(EventStatus.PUBLISHED);

        return mapToResponse(
                eventRepository.save(event));
    }

    @Override
    public EventResponse cancelEvent(
            Long eventId) {

        Event event = findEvent(eventId);

        if (event.getStatus()
                == EventStatus.COMPLETED) {

            throw new IllegalStateException(
                "Completed event cannot be cancelled");
        }

        event.setStatus(EventStatus.CANCELLED);

        return mapToResponse(
                eventRepository.save(event));
    }

    @Override
    public EventResponse completeEvent(
            Long eventId) {

        Event event = findEvent(eventId);

        if (event.getStatus()
                != EventStatus.PUBLISHED) {

            throw new IllegalStateException(
                "Only published events can be completed");
        }

        event.setStatus(EventStatus.COMPLETED);

        return mapToResponse(
                eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long eventId) {

        Event event = findEvent(eventId);

        if (event.getStatus()
                != EventStatus.DRAFT) {

            throw new IllegalStateException(
                "Only draft events can be deleted");
        }

        eventRepository.delete(event);
    }

    private Event findEvent(Long eventId) {

        return eventRepository
                .findById(eventId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Event not found with id: "
                        + eventId));
    }

    private void validateCommunity(
            Long communityId) {

        if (!communityRepository
                .existsById(communityId)) {

            throw new ResourceNotFoundException(
                "Community not found with id: "
                + communityId);
        }
    }

    private void validateDates(
            EventRequest request) {

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                "End time must be after start time");
        }

        if (request.getRegistrationDeadline()
                != null
                && request
                    .getRegistrationDeadline()
                    .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                "Registration deadline must be before event start time");
        }
    }

    private void mapRequest(
            Event event,
            EventRequest request) {

        event.setTitle(request.getTitle());
        event.setDescription(
                request.getDescription());
        event.setCategory(
                request.getCategory());
        event.setVenue(
                request.getVenue());
        event.setStartTime(
                request.getStartTime());
        event.setEndTime(
                request.getEndTime());
        event.setRegistrationDeadline(
                request.getRegistrationDeadline());
        event.setMaxParticipants(
                request.getMaxParticipants());
    }

    private EventResponse mapToResponse(
            Event event) {

        EventResponse response =
                new EventResponse();

        response.setEventId(event.getEventId());
        response.setTitle(event.getTitle());
        response.setDescription(
                event.getDescription());
        response.setCategory(
                event.getCategory());
        response.setVenue(event.getVenue());
        response.setStartTime(
                event.getStartTime());
        response.setEndTime(
                event.getEndTime());
        response.setRegistrationDeadline(
                event.getRegistrationDeadline());
        response.setMaxParticipants(
                event.getMaxParticipants());
        response.setStatus(event.getStatus());

        response.setCommunityId(
                event.getCommunity()
                    .getCommunityId());

        response.setCreatedAt(
                event.getCreatedAt());

        long count =
                registrationRepository
                    .countByEventEventIdAndStatus(
                        event.getEventId(),
                        RegistrationStatus.REGISTERED);

        response.setRegisteredParticipants(
                count);

        return response;
    }
}