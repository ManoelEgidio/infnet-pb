package br.com.manoelegidio.tp1.taskmanager.service;

import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp1.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO request);
    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);
    TaskResponseDTO updateTaskStatus(Long id, TaskStatus status);
    void deleteTask(Long id);
    TaskResponseDTO getTaskById(Long id);
    List<TaskResponseDTO> getAllTasks(TaskStatus status, Priority priority);
    DashboardMetricsDTO getMetrics();
}
