package br.com.manoelegidio.tp3.taskmanager.domain.model;

public enum ActionType {
    CREATED("Criação"),
    STATUS_CHANGED("Alteração de Status"),
    DETAILS_UPDATED("Atualização de Detalhes"),
    PRIORITY_CHANGED("Alteração de Prioridade"),
    DELETED("Exclusão");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
