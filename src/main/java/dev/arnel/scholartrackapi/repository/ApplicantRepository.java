package dev.arnel.scholartrackapi.repository;

import dev.arnel.scholartrackapi.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, String> {

    Optional<Applicant> findByOidcSubject(String oidcSubject);

    boolean existsByOidcSubject(String oidcSubject);
}