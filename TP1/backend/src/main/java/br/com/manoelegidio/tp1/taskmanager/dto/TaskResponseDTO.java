package br.com.manoelegidio.tp1.taskmanager.dto;

import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.Task;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import java.time.LocalDateTime;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    Priority priority,
    TaskStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime dueDate
) {
    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(
            task.getId(),
            task.getTitle().getValue(),
            task.getDescription(),
            task.getPriority(),
            task.getStatus(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getDueDate()
        );
    }
}
