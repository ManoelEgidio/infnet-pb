package br.com.manoelegidio.tp5.taskmanager.service;

import br.com.manoelegidio.tp5.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp5.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskResponseDTO;

import java.util.List;
import java.util.Map;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    TaskResponseDTO updateTaskStatus(Long id, TaskStatus status, String updatedBy);

    void deleteTask(Long id, String deletedBy);

    TaskResponseDTO getTaskById(Long id);

    List<TaskResponseDTO> getAllTasks(TaskStatus status, Priority priority, Long categoryId);

    List<TaskHistoryDTO> getTaskHistory(Long id);

    DashboardMetricsDTO getMetrics();

    // Cenários de Simulação para Demonstração de EDA com RabbitMQ (Critério de Rubrica TP5)
    Map<String, Object> simulateBurstTraffic(int count);

    Map<String, Object> simulatePoisonPill(Long taskId);

    Map<String, Object> simulateBroadcastAlert(String message, String actor);
}
