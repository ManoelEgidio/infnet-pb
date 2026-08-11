package br.com.manoelegidio.tp1.taskmanager.service;

import br.com.manoelegidio.tp1.taskmanager.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.Task;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskTitle;
import br.com.manoelegidio.tp1.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp1.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        TaskTitle title = new TaskTitle(request.title());
        Task task = new Task(title, request.description(), request.priority(), request.dueDate());
        
        if (request.status() != null) {
            task.updateStatus(request.status());
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = findTaskOrThrow(id);
        TaskTitle newTitle = request.title() != null ? new TaskTitle(request.title()) : null;
        
        task.updateDetails(newTitle, request.description(), request.priority(), request.dueDate());
        
        if (request.status() != null) {
            task.updateStatus(request.status());
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus status) {
        Task task = findTaskOrThrow(id);
        task.updateStatus(status);
        Task updatedTask = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return TaskResponseDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks(TaskStatus status, Priority priority) {
        List<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(priority);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO getMetrics() {
        long total = taskRepository.count();
        long pending = taskRepository.countByStatus(TaskStatus.PENDING);
        long inProgress = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completed = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long urgent = taskRepository.countByPriority(Priority.URGENT);

        return new DashboardMetricsDTO(total, pending, inProgress, completed, urgent);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
    }
}
