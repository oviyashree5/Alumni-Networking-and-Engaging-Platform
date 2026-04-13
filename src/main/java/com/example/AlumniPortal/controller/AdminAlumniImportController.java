package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.AlumniImportResponse;
import com.example.AlumniPortal.dto.AlumniManualCreateRequest;
import com.example.AlumniPortal.dto.AlumniRegistryItemResponse;
import com.example.AlumniPortal.entity.Alumni;
import com.example.AlumniPortal.service.AlumniRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/alumni")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAlumniImportController {

    private final AlumniRegistryService alumniRegistryService;

    @PostMapping("/upload-csv")
    public AlumniImportResponse uploadCsv(@RequestPart("file") MultipartFile file) {
        return alumniRegistryService.importCsv(file);
    }

    @GetMapping
    public List<AlumniRegistryItemResponse> getPreloadedAlumni() {
        return alumniRegistryService.getRegistryEntries();
    }

    @PostMapping
    public Alumni addSingleAlumni(@Valid @RequestBody AlumniManualCreateRequest request) {
        return alumniRegistryService.addSingleAlumni(request);
    }

    @DeleteMapping("/{id}")
    public String deletePreloadedAlumni(@PathVariable Long id) {
        alumniRegistryService.deletePreloadedAlumni(id);
        return "Preloaded alumni deleted successfully";
    }
}
