package br.com.manoelegidio.tp3.notification.domain.model;

public enum NotificationType {
    TASK_CREATED("Nova Tarefa Criada"),
    STATUS_CHANGED("Status da Tarefa Alterado"),
    HIGH_PRIORITY_ALERT("Alerta de Prioridade Alta"),
    TASK_COMPLETED("Tarefa Concluída com Sucesso"),
    SYSTEM_ALERT("Alerta do Sistema");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
