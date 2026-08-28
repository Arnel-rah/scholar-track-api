package dev.arnel.scholartrackapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applicants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_applicant_oidc_subject", columnNames = "oidc_subject"),
        @UniqueConstraint(name = "uk_applicant_email", columnNames = "email")
})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Applicant {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false, length = 36)
    private String id;


    @Column(name = "oidc_subject", nullable = false, length = 255)
    private String oidcSubject;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}