package dev.arnel.scholartrackapi.repository;
import dev.arnel.scholartrackapi.entity.Scholarship;
import dev.arnel.scholartrackapi.entity.enums.ScholarshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScholarshipRepository extends JpaRepository<Scholarship, String> {

    List<Scholarship> findByStatus(ScholarshipStatus status);

    List<Scholarship> findByDeadlineLessThanEqualOrderByDeadlineAsc(LocalDate date);
}