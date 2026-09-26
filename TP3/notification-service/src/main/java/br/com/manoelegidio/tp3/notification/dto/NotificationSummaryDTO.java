package br.com.manoelegidio.tp3.notification.dto;

public class NotificationSummaryDTO {

    private long totalCount;
    private long unreadCount;
    private long readCount;
    private String recipient;

    public NotificationSummaryDTO() {
    }

    public NotificationSummaryDTO(long totalCount, long unreadCount, long readCount, String recipient) {
        this.totalCount = totalCount;
        this.unreadCount = unreadCount;
        this.readCount = readCount;
        this.recipient = recipient;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
