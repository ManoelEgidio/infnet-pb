package br.com.manoelegidio.tp5.notification.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO de evento recebido do RabbitMQ via padrão Event-Carried State Transfer.
 */
public class TaskEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventType; // TASK_CREATED, TASK_UPDATED, TASK_STATUS_CHANGED, TASK_COMPLETED, TASK_DELETED, SYSTEM_BROADCAST
    private Long taskId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String categoryName;
    private String dueDate;
    private String actor;
    private LocalDateTime timestamp;
    private boolean simulateError;

    public TaskEventDTO() {}

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSimulateError() { return simulateError; }
    public void setSimulateError(boolean simulateError) { this.simulateError = simulateError; }

    @Override
    public String toString() {
        return "TaskEventDTO{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", taskId=" + taskId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", simulateError=" + simulateError +
                '}';
    }
}
