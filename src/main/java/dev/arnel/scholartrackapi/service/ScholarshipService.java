package dev.arnel.scholartrackapi.service;


import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import dev.arnel.scholartrackapi.dto.ScholarshipResponse;
import dev.arnel.scholartrackapi.entity.Scholarship;
import dev.arnel.scholartrackapi.entity.enums.ScholarshipStatus;
import dev.arnel.scholartrackapi.exception.ResourceNotFoundException;
import dev.arnel.scholartrackapi.mapper.ScholarshipMapper;
import dev.arnel.scholartrackapi.repository.ScholarshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;

    public List<ScholarshipResponse> listOpen() {
        return scholarshipRepository.findByStatus(ScholarshipStatus.OPEN)
                .stream()
                .map(ScholarshipMapper::toResponse)
                .toList();
    }

    public ScholarshipResponse getByIdOrThrow(String id) {
        return ScholarshipMapper.toResponse(findEntityOrThrow(id));
    }

    Scholarship findEntityOrThrow(String id) {
        return scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found: " + id));
    }

    @Transactional
    public ScholarshipResponse create(ScholarshipRequest request) {
        Scholarship scholarship = ScholarshipMapper.toEntity(request);
        return ScholarshipMapper.toResponse(scholarshipRepository.save(scholarship));
    }

    public List<ScholarshipResponse> listWithDeadlineBefore(LocalDate date) {
        return scholarshipRepository.findByDeadlineLessThanEqualOrderByDeadlineAsc(date)
                .stream()
                .map(ScholarshipMapper::toResponse)
                .toList();
    }
}
