package br.com.manoelegidio.tp3.taskmanager.client.dto;

public class NotificationPayloadDTO {

    private Long taskId;
    private String taskTitle;
    private String recipient;
    private String message;
    private String type; // TASK_CREATED, STATUS_CHANGED, HIGH_PRIORITY_ALERT, TASK_COMPLETED, SYSTEM_ALERT
    private String channel; // IN_APP, EMAIL, WEBHOOK

    public NotificationPayloadDTO() {
    }

    public NotificationPayloadDTO(Long taskId, String taskTitle, String recipient, String message, String type, String channel) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
