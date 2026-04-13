package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.AnnouncementRequest;
import com.example.AlumniPortal.dto.AnnouncementResponse;
import com.example.AlumniPortal.entity.Announcement;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.repository.AnnouncementRepository;
import com.example.AlumniPortal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    // 🔹 Only ADMIN will call this
    @Transactional
    public AnnouncementResponse createAnnouncement(Long adminId, AnnouncementRequest request) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .priority(request.getPriority())
                .createdAt(LocalDateTime.now())
                .user(admin)
                .build();

        announcementRepository.save(announcement);

        return mapToResponse(announcement);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll()
                .stream()
                .sorted(announcementNewestFirst())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> filterAnnouncements(String category, String priority) {

        List<Announcement> list;

        if (category != null && priority != null)
            list = announcementRepository.findByCategoryAndPriority(category, priority);
        else if (category != null)
            list = announcementRepository.findByCategoryIgnoreCase(category);
        else if (priority != null)
            list = announcementRepository.findByPriorityIgnoreCase(priority);
        else
            list = announcementRepository.findAll();

        return list.stream()
                .sorted(announcementNewestFirst())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementResponse mapToResponse(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .category(a.getCategory())
                .priority(a.getPriority())
                .authorName(a.getUser().getEmail())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private Comparator<Announcement> announcementNewestFirst() {
        return Comparator
                .comparing(Announcement::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Announcement::getId, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
