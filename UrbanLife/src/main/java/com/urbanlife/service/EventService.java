package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.EventRequest;
import com.urbanlife.dto.EventResponse;
import com.urbanlife.enums.EventStatus;

public interface EventService {

    EventResponse createEvent(
            EventRequest request);

    EventResponse getEventById(
            Long eventId);

    List<EventResponse> getCommunityEvents(
            Long communityId);

    List<EventResponse> getEventsByStatus(
            Long communityId,
            EventStatus status);

    EventResponse updateEvent(
            Long eventId,
            EventRequest request);

    EventResponse publishEvent(
            Long eventId);

    EventResponse cancelEvent(
            Long eventId);

    EventResponse completeEvent(
            Long eventId);

    void deleteEvent(
            Long eventId);
}