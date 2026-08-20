package br.com.manoelegidio.tp2.taskmanager.service;

import br.com.manoelegidio.tp2.taskmanager.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp2.taskmanager.domain.model.*;
import br.com.manoelegidio.tp2.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp2.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CategoryService categoryService;
    private final TaskHistoryService historyService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           CategoryService categoryService,
                           TaskHistoryService historyService) {
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
        this.historyService = historyService;
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        TaskTitle title = new TaskTitle(request.title());
        Category category = request.categoryId() != null ? categoryService.getCategoryEntityById(request.categoryId()) : null;

        Task task = new Task(title, request.description(), request.priority(), request.dueDate(), category);
        
        if (request.status() != null) {
            task.updateStatus(request.status());
        }

        Task savedTask = taskRepository.save(task);

        // Registra histórico de criação
        String author = request.updatedBy() != null ? request.updatedBy() : "Sistema";
        historyService.recordHistory(
                savedTask.getId(),
                ActionType.CREATED,
                "TASK",
                null,
                savedTask.getTitle().getValue(),
                "Tarefa criada com prioridade " + savedTask.getPriority() + " e status " + savedTask.getStatus(),
                author
        );

        return TaskResponseDTO.fromEntity(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = findTaskOrThrow(id);
        String author = request.updatedBy() != null ? request.updatedBy() : "Sistema";

        // Comparações para histórico detalhado
        TaskTitle newTitle = request.title() != null ? new TaskTitle(request.title()) : null;
        Category newCategory = request.categoryId() != null ? categoryService.getCategoryEntityById(request.categoryId()) : null;

        if (newTitle != null && !task.getTitle().getValue().equals(newTitle.getValue())) {
            historyService.recordHistory(
                    task.getId(),
                    ActionType.DETAILS_UPDATED,
                    "title",
                    task.getTitle().getValue(),
                    newTitle.getValue(),
                    "Título alterado de '" + task.getTitle().getValue() + "' para '" + newTitle.getValue() + "'",
                    author
            );
        }

        if (request.priority() != null && task.getPriority() != request.priority()) {
            historyService.recordHistory(
                    task.getId(),
                    ActionType.PRIORITY_CHANGED,
                    "priority",
                    task.getPriority().name(),
                    request.priority().name(),
                    "Prioridade alterada de " + task.getPriority() + " para " + request.priority(),
                    author
            );
        }

        if (request.status() != null && task.getStatus() != request.status()) {
            historyService.recordHistory(
                    task.getId(),
                    ActionType.STATUS_CHANGED,
                    "status",
                    task.getStatus().name(),
                    request.status().name(),
                    "Status alterado de " + task.getStatus() + " para " + request.status(),
                    author
            );
            task.updateStatus(request.status());
        }

        task.updateDetails(newTitle, request.description(), request.priority(), request.dueDate(), newCategory);
        Task updatedTask = taskRepository.save(task);

        return TaskResponseDTO.fromEntity(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus status, String updatedBy) {
        Task task = findTaskOrThrow(id);
        if (status != null && task.getStatus() != status) {
            String author = updatedBy != null ? updatedBy : "Sistema";
            historyService.recordHistory(
                    task.getId(),
                    ActionType.STATUS_CHANGED,
                    "status",
                    task.getStatus().name(),
                    status.name(),
                    "Status atualizado diretamente para " + status.name(),
                    author
            );
            task.updateStatus(status);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updatedTask);
    }

    @Override
    public void deleteTask(Long id, String deletedBy) {
        Task task = findTaskOrThrow(id);
        String author = deletedBy != null ? deletedBy : "Sistema";

        historyService.recordHistory(
                task.getId(),
                ActionType.DELETED,
                "TASK",
                task.getTitle().getValue(),
                null,
                "Tarefa deletada do sistema.",
                author
        );

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
    public List<TaskResponseDTO> getAllTasks(TaskStatus status, Priority priority, Long categoryId) {
        List<Task> tasks = taskRepository.findByFilters(status, priority, categoryId);

        return tasks.stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistoryDTO> getTaskHistory(Long id) {
        // Valida se a tarefa existe ou existiu
        return historyService.getHistoryByTaskId(id);
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
