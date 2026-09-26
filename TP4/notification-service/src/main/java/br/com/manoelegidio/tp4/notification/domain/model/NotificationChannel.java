package br.com.manoelegidio.tp4.notification.domain.model;

public enum NotificationChannel {
    IN_APP("Notificação no Sistema"),
    EMAIL("Mensagem de E-mail"),
    WEBHOOK("Disparo de Webhook");

    private final String label;

    NotificationChannel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
