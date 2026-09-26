package br.com.manoelegidio.tp4.taskmanager.dto;

import br.com.manoelegidio.tp4.taskmanager.domain.model.Category;

import java.time.LocalDateTime;

public record CategoryDTO(
        Long id,
        String name,
        String description,
        String colorCode,
        LocalDateTime createdAt
) {
    public static CategoryDTO fromEntity(Category category) {
        if (category == null) return null;
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColorCode(),
                category.getCreatedAt()
        );
    }
}
