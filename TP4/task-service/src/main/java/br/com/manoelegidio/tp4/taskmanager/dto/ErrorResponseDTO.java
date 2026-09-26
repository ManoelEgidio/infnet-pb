package br.com.manoelegidio.tp4.taskmanager.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}
