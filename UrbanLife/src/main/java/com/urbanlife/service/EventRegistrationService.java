package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.EventRegistrationResponse;

public interface EventRegistrationService {

    EventRegistrationResponse register(
            Long eventId,
            Long userId);

    EventRegistrationResponse cancelRegistration(
            Long eventId,
            Long userId);

    EventRegistrationResponse markAttendance(
            Long eventId,
            Long userId);

    List<EventRegistrationResponse>
        getEventRegistrations(
            Long eventId);

    List<EventRegistrationResponse>
        getUserRegistrations(
            Long userId);
}