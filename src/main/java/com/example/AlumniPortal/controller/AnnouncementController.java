package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.AnnouncementRequest;
import com.example.AlumniPortal.dto.AnnouncementResponse;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // ✅ ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementResponse createAnnouncement(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AnnouncementRequest request) {

        return announcementService.createAnnouncement(userDetails.getUserId(), request);
    }

    // ✅ Everyone can view
    @GetMapping
    public List<AnnouncementResponse> getAll() {
        return announcementService.getAllAnnouncements();
    }

    // ✅ Filter support
    @GetMapping("/filter")
    public List<AnnouncementResponse> filter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority) {

        return announcementService.filterAnnouncements(category, priority);
    }

    // ✅ ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "Announcement deleted successfully";
    }
}
