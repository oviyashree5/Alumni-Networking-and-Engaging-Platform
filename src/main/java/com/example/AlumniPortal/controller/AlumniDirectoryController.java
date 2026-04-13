package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.AlumniDirectoryResponse;
import com.example.AlumniPortal.service.AlumniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
public class AlumniDirectoryController {

    private final AlumniProfileService alumniProfileService;

    @GetMapping
    public List<AlumniDirectoryResponse> getDirectory() {
        return alumniProfileService.getDirectory();
    }
}
