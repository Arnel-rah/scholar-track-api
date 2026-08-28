package dev.arnel.scholartrackapi.entity;

import dev.arnel.scholartrackapi.entity.enums.ScholarshipStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scholarships")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Scholarship {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false, length = 36)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "official_url", length = 500)
    private String officialUrl;

    @Column(name = "opens_at")
    private LocalDate opensAt;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScholarshipStatus status = ScholarshipStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "scholarship")
    private List<Application> applications = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}