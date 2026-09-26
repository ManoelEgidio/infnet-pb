package br.com.manoelegidio.tp3.taskmanager.service;

import br.com.manoelegidio.tp3.taskmanager.client.NotificationClient;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationPayloadDTO;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationResponseClientDTO;
import br.com.manoelegidio.tp3.taskmanager.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp3.taskmanager.domain.model.*;
import br.com.manoelegidio.tp3.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp3.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp3.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp3.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp3.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final CategoryService categoryService;
    private final TaskHistoryService historyService;
    private final NotificationClient notificationClient;

    public TaskServiceImpl(TaskRepository taskRepository,
                           CategoryService categoryService,
                           TaskHistoryService historyService,
                           NotificationClient notificationClient) {
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
        this.historyService = historyService;
        this.notificationClient = notificationClient;
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

        // Dispara notificação distribuída para o microsserviço notification-service via Feign
        if (savedTask.getPriority() == Priority.HIGH || savedTask.getPriority() == Priority.URGENT) {
            publishNotification(
                    savedTask.getId(),
                    savedTask.getTitle().getValue(),
                    author,
                    "Atenção: Nova tarefa urgente/alta prioridade criada: '" + savedTask.getTitle().getValue() + "'.",
                    "HIGH_PRIORITY_ALERT"
            );
        } else {
            publishNotification(
                    savedTask.getId(),
                    savedTask.getTitle().getValue(),
                    author,
                    "Nova tarefa cadastrada com sucesso: '" + savedTask.getTitle().getValue() + "'.",
                    "TASK_CREATED"
            );
        }

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

        boolean statusChanged = false;
        if (request.status() != null && task.getStatus() != request.status()) {
            statusChanged = true;
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

        if (statusChanged) {
            String type = updatedTask.getStatus() == TaskStatus.COMPLETED ? "TASK_COMPLETED" : "STATUS_CHANGED";
            publishNotification(
                    updatedTask.getId(),
                    updatedTask.getTitle().getValue(),
                    author,
                    "A tarefa '" + updatedTask.getTitle().getValue() + "' foi alterada para o status " + updatedTask.getStatus() + ".",
                    type
            );
        }

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

            String type = status == TaskStatus.COMPLETED ? "TASK_COMPLETED" : "STATUS_CHANGED";
            publishNotification(
                    task.getId(),
                    task.getTitle().getValue(),
                    author,
                    "Status da tarefa '" + task.getTitle().getValue() + "' atualizado para " + status + " por " + author + ".",
                    type
            );
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

        publishNotification(
                task.getId(),
                task.getTitle().getValue(),
                author,
                "A tarefa '" + task.getTitle().getValue() + "' foi removida do sistema por " + author + ".",
                "SYSTEM_ALERT"
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

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseClientDTO> getTaskNotifications(Long taskId) {
        findTaskOrThrow(taskId);
        try {
            return notificationClient.getNotificationsByTaskId(taskId);
        } catch (Exception e) {
            log.warn("Erro ao obter notificações distribuídas para a tarefa {}: {}", taskId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private void publishNotification(Long taskId, String taskTitle, String recipient, String message, String type) {
        try {
            NotificationPayloadDTO payload = new NotificationPayloadDTO(
                    taskId,
                    taskTitle,
                    recipient != null && !recipient.isBlank() ? recipient : "manoel@infnet.edu.br",
                    message,
                    type,
                    "IN_APP"
            );
            notificationClient.sendNotification(payload);
        } catch (Exception e) {
            log.warn("Falha não bloqueante na comunicação distribuída com o notification-service: {}", e.getMessage());
        }
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
    }
}
