package br.com.manoelegidio.tp4.taskmanager.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    protected Category() {
        // Exigido pelo JPA
    }

    public Category(String name, String description, String colorCode) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        this.name = name.trim();
        this.description = description;
        this.colorCode = colorCode != null ? colorCode : "#3b82f6";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
