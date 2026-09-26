package br.com.manoelegidio.tp5.taskmanager.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_task_history", indexes = {
        @Index(name = "idx_history_task_id", columnList = "task_id"),
        @Index(name = "idx_history_changed_at", columnList = "changed_at")
})
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 40)
    private ActionType actionType;

    @Column(name = "field_changed", length = 80)
    private String fieldChanged;

    @Column(name = "old_value", length = 500)
    private String oldValue;

    @Column(name = "new_value", length = 500)
    private String newValue;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "changed_by", length = 100, nullable = false)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected TaskHistory() {
        // Exigido pelo JPA
    }

    public TaskHistory(Long taskId, ActionType actionType, String fieldChanged, String oldValue, String newValue, String details, String changedBy) {
        this.taskId = taskId;
        this.actionType = actionType;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.details = details;
        this.changedBy = (changedBy != null && !changedBy.trim().isEmpty()) ? changedBy : "Sistema / Usuário";
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getDetails() {
        return details;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
