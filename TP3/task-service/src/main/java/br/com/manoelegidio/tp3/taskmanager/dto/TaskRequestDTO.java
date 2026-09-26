package br.com.manoelegidio.tp3.taskmanager.dto;

import br.com.manoelegidio.tp3.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp3.taskmanager.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequestDTO(
        @NotBlank(message = "O título da tarefa é obrigatório.")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres.")
        String title,

        @Size(max = 500, message = "A descrição não pode exceder 500 caracteres.")
        String description,

        Priority priority,

        TaskStatus status,

        LocalDateTime dueDate,

        Long categoryId,

        String updatedBy
) {
}
