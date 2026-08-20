package br.com.manoelegidio.tp2.taskmanager.dto;

public record DashboardMetricsDTO(
    long totalTasks,
    long pendingTasks,
    long inProgressTasks,
    long completedTasks,
    long urgentTasks
) {}
