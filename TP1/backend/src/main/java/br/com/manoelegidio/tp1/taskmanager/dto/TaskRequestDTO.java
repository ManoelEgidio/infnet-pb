package br.com.manoelegidio.tp1.taskmanager.dto;

import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TaskRequestDTO(
    @NotBlank(message = "O título é obrigatório.")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres.")
    String title,

    @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres.")
    String description,

    Priority priority,
    TaskStatus status,
    LocalDateTime dueDate
) {}
