package br.com.manoelegidio.tp5.notification.dto;

import br.com.manoelegidio.tp5.notification.domain.model.Notification;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private String recipient;
    private String message;
    private NotificationType type;
    private String typeDescription;
    private NotificationChannel channel;
    private String channelLabel;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public NotificationResponseDTO() {
    }

    public static NotificationResponseDTO fromEntity(Notification entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setTaskId(entity.getTaskId());
        dto.setTaskTitle(entity.getTaskTitle());
        dto.setRecipient(entity.getRecipient());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setTypeDescription(entity.getType() != null ? entity.getType().getDescription() : "");
        dto.setChannel(entity.getChannel());
        dto.setChannelLabel(entity.getChannel() != null ? entity.getChannel().getLabel() : "");
        dto.setRead(entity.isRead());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setReadAt(entity.getReadAt());
        return dto;
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

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getChannelLabel() {
        return channelLabel;
    }

    public void setChannelLabel(String channelLabel) {
        this.channelLabel = channelLabel;
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
