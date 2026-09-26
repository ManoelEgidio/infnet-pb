package br.com.manoelegidio.tp5.taskmanager.dto;

import br.com.manoelegidio.tp5.taskmanager.domain.model.ActionType;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskHistory;

import java.time.LocalDateTime;

public record TaskHistoryDTO(
        Long id,
        Long taskId,
        ActionType actionType,
        String actionDescription,
        String fieldChanged,
        String oldValue,
        String newValue,
        String details,
        String changedBy,
        LocalDateTime changedAt
) {
    public static TaskHistoryDTO fromEntity(TaskHistory history) {
        if (history == null) return null;
        return new TaskHistoryDTO(
                history.getId(),
                history.getTaskId(),
                history.getActionType(),
                history.getActionType() != null ? history.getActionType().getDescription() : "",
                history.getFieldChanged(),
                history.getOldValue(),
                history.getNewValue(),
                history.getDetails(),
                history.getChangedBy(),
                history.getChangedAt()
        );
    }
}
