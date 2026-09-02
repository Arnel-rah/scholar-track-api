package dev.arnel.scholartrackapi.controller;

import dev.arnel.scholartrackapi.dto.ScholarshipRequest;
import dev.arnel.scholartrackapi.dto.ScholarshipResponse;
import dev.arnel.scholartrackapi.service.ScholarshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    @GetMapping
    public List<ScholarshipResponse> list(
            @RequestParam(name = "deadlineBefore", required = false) LocalDate deadlineBefore) {
        return deadlineBefore != null
                ? scholarshipService.listWithDeadlineBefore(deadlineBefore)
                : scholarshipService.listOpen();
    }

    @GetMapping("/{id}")
    public ScholarshipResponse get(@PathVariable String id) {
        return scholarshipService.getByIdOrThrow(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScholarshipResponse create(@Valid @RequestBody ScholarshipRequest request) {
        return scholarshipService.create(request);
    }
}
