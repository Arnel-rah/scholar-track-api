package dev.arnel.scholartrackapi.repository;

import dev.arnel.scholartrackapi.entity.Application;
import dev.arnel.scholartrackapi.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, String> {

    List<Application> findByApplicantId(String applicantId);

    Optional<Application> findByIdAndApplicantId(String id, String applicantId);

    boolean existsByApplicantIdAndScholarshipId(String applicantId, String scholarshipId);

    long countByApplicantIdAndStatus(String applicantId, ApplicationStatus status);

    @Query("""
            SELECT a FROM Application a
            JOIN FETCH a.scholarship
            LEFT JOIN FETCH a.documents
            WHERE a.applicant.id = :applicantId
            """)
    List<Application> findAllByApplicantIdWithDetails(@Param("applicantId") String applicantId);
}
