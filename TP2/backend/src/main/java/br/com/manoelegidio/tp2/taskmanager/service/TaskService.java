package br.com.manoelegidio.tp2.taskmanager.service;

import br.com.manoelegidio.tp2.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp2.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp2.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    TaskResponseDTO updateTaskStatus(Long id, TaskStatus status, String updatedBy);

    void deleteTask(Long id, String deletedBy);

    TaskResponseDTO getTaskById(Long id);

    List<TaskResponseDTO> getAllTasks(TaskStatus status, Priority priority, Long categoryId);

    List<TaskHistoryDTO> getTaskHistory(Long id);

    DashboardMetricsDTO getMetrics();
}
