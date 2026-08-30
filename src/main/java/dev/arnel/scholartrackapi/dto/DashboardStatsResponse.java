package dev.arnel.scholartrackapi.dto;


import java.util.List;

public record DashboardStatsResponse(
        long totalApplications,
        long inProgress,
        long submitted,
        List<UpcomingDeadline> upcomingDeadlines
) {
    public record UpcomingDeadline(
            String scholarshipName,
            java.time.LocalDate deadline,
            long daysRemaining
    ) {}
}
