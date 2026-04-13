package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.EventRequest;
import com.example.AlumniPortal.dto.EventResponse;
import com.example.AlumniPortal.entity.EventRegistration;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.EventService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        return service.createEvent(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse updateEvent(@PathVariable Long id,
                                     @Valid @RequestBody EventRequest request) {
        return service.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ALUMNI')")
    public List<EventResponse> getAllEvents(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return service.getAllEventsForAdmin();
        }
        return service.getEventsForAlumni(user.getUserId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ALUMNI')")
    public EventResponse getEventById(@PathVariable Long id) {
        return service.getEventById(id);
    }

    @PostMapping("/{eventId}/register")
    @PreAuthorize("hasRole('ALUMNI')")
    public String registerForEvent(@PathVariable Long eventId,
                                   Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return service.registerForEvent(eventId, user.getUserId());
    }

    @GetMapping("/my-registrations")
    @PreAuthorize("hasRole('ALUMNI')")
    public List<EventRegistration> myRegistrations(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return service.getMyRegistrations(user.getUserId());
    }

    @GetMapping("/{eventId}/registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EventRegistration> getEventRegistrations(@PathVariable Long eventId) {
        return service.getEventRegistrations(eventId);
    }
}
