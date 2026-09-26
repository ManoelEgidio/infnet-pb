package br.com.manoelegidio.tp5.taskmanager.service;

import br.com.manoelegidio.tp5.taskmanager.domain.model.ActionType;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskHistory;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp5.taskmanager.repository.TaskHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskHistoryService {

    private final TaskHistoryRepository historyRepository;

    public TaskHistoryService(TaskHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordHistory(Long taskId, ActionType actionType, String fieldChanged, String oldValue, String newValue, String details, String changedBy) {
        TaskHistory history = new TaskHistory(taskId, actionType, fieldChanged, oldValue, newValue, details, changedBy);
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryDTO> getHistoryByTaskId(Long taskId) {
        return historyRepository.findByTaskIdOrderByChangedAtDesc(taskId)
                .stream()
                .map(TaskHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryDTO> getAllHistory() {
        return historyRepository.findAll()
                .stream()
                .map(TaskHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
