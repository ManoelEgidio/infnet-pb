package br.com.manoelegidio.tp5.taskmanager.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}
