package br.com.manoelegidio.tp3.notification.dto;

import br.com.manoelegidio.tp3.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp3.notification.domain.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequestDTO {

    private Long taskId;
    private String taskTitle;

    @NotBlank(message = "O destinatário é obrigatório.")
    private String recipient;

    @NotBlank(message = "A mensagem da notificação é obrigatória.")
    private String message;

    @NotNull(message = "O tipo da notificação é obrigatório.")
    private NotificationType type;

    private NotificationChannel channel;

    public NotificationRequestDTO() {
    }

    public NotificationRequestDTO(Long taskId, String taskTitle, String recipient,
                                  String message, NotificationType type, NotificationChannel channel) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.channel = channel;
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
}
