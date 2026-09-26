package br.com.manoelegidio.tp4.taskmanager.dto;

import br.com.manoelegidio.tp4.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp4.taskmanager.domain.model.Task;
import br.com.manoelegidio.tp4.taskmanager.domain.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Priority priority,
        TaskStatus status,
        CategoryDTO category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime dueDate
) {
    public static TaskResponseDTO fromEntity(Task task) {
        if (task == null) return null;
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle() != null ? task.getTitle().getValue() : null,
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                CategoryDTO.fromEntity(task.getCategory()),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDueDate()
        );
    }
}
