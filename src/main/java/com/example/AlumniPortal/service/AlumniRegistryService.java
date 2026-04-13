package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.AlumniImportResponse;
import com.example.AlumniPortal.dto.AlumniManualCreateRequest;
import com.example.AlumniPortal.dto.AlumniRegistryItemResponse;
import com.example.AlumniPortal.entity.Alumni;
import com.example.AlumniPortal.entity.AlumniRegistrationStatus;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.AlumniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlumniRegistryService {

    private final AlumniRepository alumniRepository;

    @Cacheable(value = "alumniEmails", key = "'all'")
    public Set<String> getCachedAlumniEmails() {
        return alumniRepository.findAll()
                .stream()
                .map(Alumni::getEmail)
                .filter(Objects::nonNull)
                .map(this::normalizeEmail)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    public Optional<Alumni> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return alumniRepository.findByEmailIgnoreCase(normalizeEmail(email));
    }

    public boolean isPreloadedAlumniEmail(String email) {
        return getCachedAlumniEmails().contains(normalizeEmail(email));
    }

    public List<AlumniRegistryItemResponse> getRegistryEntries() {
        return alumniRepository.findAll(Sort.by(Sort.Direction.DESC, "importedAt"))
                .stream()
                .map(this::mapRegistryItem)
                .toList();
    }

    @CacheEvict(value = "alumniEmails", allEntries = true)
    public AlumniImportResponse importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please upload a CSV file");
        }

        validateCsvFile(file);

        int totalRows = 0;
        int imported = 0;
        int updated = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new BadRequestException("CSV file is empty");
            }

            Map<String, Integer> headerIndex = buildHeaderIndex(headerLine);
            validateRequiredHeaders(headerIndex);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                totalRows++;
                String[] columns = splitCsv(line);

                String email = readColumn(columns, headerIndex, "email", 0);
                String name = readColumn(columns, headerIndex, "name", 1);
                String batchYear = readColumn(columns, headerIndex, "batchyear", 2);
                String department = readColumn(columns, headerIndex, "department", 3);

                if (email == null || email.isBlank() || name == null || name.isBlank()) {
                    skipped++;
                    continue;
                }

                String normalizedEmail = normalizeEmail(email);
                Optional<Alumni> existingOptional = alumniRepository.findByEmailIgnoreCase(normalizedEmail);

                saveImportedAlumni(existingOptional, normalizedEmail, name, batchYear, department);
                if (existingOptional.isPresent()) {
                    updated++;
                } else {
                    imported++;
                }
            }
        } catch (IOException exception) {
            throw new BadRequestException("Unable to read the uploaded CSV file");
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("Unable to import the file. Please upload a valid CSV with columns: email,name,batchYear,department");
        }

        return new AlumniImportResponse(
                totalRows,
                imported,
                updated,
                skipped,
                "CSV processed successfully"
        );
    }

    @CacheEvict(value = "alumniEmails", allEntries = true)
    public Alumni addSingleAlumni(AlumniManualCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        Optional<Alumni> existingOptional = alumniRepository.findByEmailIgnoreCase(normalizedEmail);
        Alumni alumni = existingOptional.orElseGet(Alumni::new);

        if (alumni.getId() == null) {
            alumni.setImportedAt(LocalDateTime.now());
            alumni.setRegistrationStatus(AlumniRegistrationStatus.NOT_REGISTERED);
        }

        alumni.setEmail(normalizedEmail);
        alumni.setName(request.getName().trim());
        alumni.setBatchYear(blankToNull(request.getBatchYear()));
        alumni.setDepartment(blankToNull(request.getDepartment()));

        return alumniRepository.save(alumni);
    }

    @CacheEvict(value = "alumniEmails", allEntries = true)
    public void deletePreloadedAlumni(Long alumniId) {
        Alumni alumni = alumniRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni preload entry not found"));

        if (AlumniRegistrationStatus.REGISTERED.equals(alumni.getRegistrationStatus())
                || alumni.getUserId() != null
                || alumni.getRegisteredAt() != null) {
            throw new BadRequestException("Registered alumni cannot be deleted from the preload registry");
        }

        alumniRepository.delete(alumni);
    }

    @CacheEvict(value = "alumniEmails", allEntries = true)
    public void markRegistered(Alumni alumni, Long userId) {
        alumni.setUserId(userId);
        alumni.setRegistrationStatus(AlumniRegistrationStatus.REGISTERED);
        alumni.setRegisteredAt(LocalDateTime.now());
        alumniRepository.save(alumni);
    }

    private Map<String, Integer> buildHeaderIndex(String headerLine) {
        String[] headers = splitCsv(headerLine);
        Map<String, Integer> headerIndex = new HashMap<>();

        for (int index = 0; index < headers.length; index++) {
            headerIndex.put(headers[index].trim().toLowerCase(Locale.ROOT), index);
        }

        return headerIndex;
    }

    private void validateCsvFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new BadRequestException("Only CSV files are supported. Please upload a .csv file instead of Excel .xlsx");
        }
    }

    private void validateRequiredHeaders(Map<String, Integer> headerIndex) {
        List<String> requiredHeaders = List.of("email", "name", "batchyear", "department");
        boolean hasRequiredHeaders = requiredHeaders.stream().allMatch(headerIndex::containsKey);
        if (!hasRequiredHeaders) {
            throw new BadRequestException("CSV must include these columns: email,name,batchYear,department");
        }
    }

    private void saveImportedAlumni(Optional<Alumni> existingOptional,
                                    String normalizedEmail,
                                    String name,
                                    String batchYear,
                                    String department) {
        if (existingOptional.isPresent()) {
            Alumni existing = existingOptional.get();
            existing.setName(name.trim());
            existing.setBatchYear(blankToNull(batchYear));
            existing.setDepartment(blankToNull(department));
            alumniRepository.save(existing);
            return;
        }

        Alumni alumni = new Alumni();
        alumni.setEmail(normalizedEmail);
        alumni.setName(name.trim());
        alumni.setBatchYear(blankToNull(batchYear));
        alumni.setDepartment(blankToNull(department));
        alumni.setRegistrationStatus(AlumniRegistrationStatus.NOT_REGISTERED);
        alumni.setImportedAt(LocalDateTime.now());
        alumniRepository.save(alumni);
    }

    private String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private String readColumn(String[] columns,
                              Map<String, Integer> headerIndex,
                              String headerName,
                              int fallbackIndex) {
        Integer index = headerIndex.getOrDefault(headerName, fallbackIndex);
        if (index == null || index < 0 || index >= columns.length) {
            return null;
        }
        return trimCsvValue(columns[index]);
    }

    private String trimCsvValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.trim();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private AlumniRegistryItemResponse mapRegistryItem(Alumni alumni) {
        return new AlumniRegistryItemResponse(
                alumni.getId(),
                alumni.getEmail(),
                alumni.getName(),
                alumni.getBatchYear(),
                alumni.getDepartment(),
                alumni.getRegistrationStatus(),
                alumni.getImportedAt(),
                alumni.getRegisteredAt()
        );
    }
}
