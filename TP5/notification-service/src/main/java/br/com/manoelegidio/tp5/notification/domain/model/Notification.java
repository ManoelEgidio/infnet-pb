package br.com.manoelegidio.tp5.notification.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_recipient", columnList = "recipient"),
    @Index(name = "idx_notifications_is_read", columnList = "is_read"),
    @Index(name = "idx_notifications_task_id", columnList = "task_id"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "task_title", length = 150)
    private String taskTitle;

    @NotBlank(message = "O destinatário da notificação é obrigatório.")
    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;

    @NotBlank(message = "A mensagem da notificação não pode ser vazia.")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @NotNull(message = "O tipo da notificação é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @NotNull(message = "O canal da notificação é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 30)
    private NotificationChannel channel;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification() {
    }

    public Notification(Long taskId, String taskTitle, String recipient, String message,
                        NotificationType type, NotificationChannel channel) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.recipient = recipient;
        this.message = message;
        this.type = type != null ? type : NotificationType.SYSTEM_ALERT;
        this.channel = channel != null ? channel : NotificationChannel.IN_APP;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.recipient == null || this.recipient.isBlank()) {
            this.recipient = "manoel@infnet.edu.br";
        }
        if (this.channel == null) {
            this.channel = NotificationChannel.IN_APP;
        }
        if (this.type == null) {
            this.type = NotificationType.SYSTEM_ALERT;
        }
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
