package br.com.manoelegidio.tp2.taskmanager.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_tasks", indexes = {
        @Index(name = "idx_task_status", columnList = "status"),
        @Index(name = "idx_task_priority", columnList = "priority"),
        @Index(name = "idx_task_category", columnList = "category_id")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false, length = 100))
    private TaskTitle title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    protected Task() {
        // Exigido pelo JPA
    }

    public Task(TaskTitle title, String description, Priority priority, LocalDateTime dueDate, Category category) {
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = TaskStatus.PENDING;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.dueDate = dueDate;
    }

    public void updateDetails(TaskTitle newTitle, String newDescription, Priority newPriority, LocalDateTime newDueDate, Category newCategory) {
        if (newTitle != null) {
            this.title = newTitle;
        }
        this.description = newDescription;
        if (newPriority != null) {
            this.priority = newPriority;
        }
        this.dueDate = newDueDate;
        this.category = newCategory;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(TaskStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public TaskTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
}
