package br.com.manoelegidio.tp5.taskmanager.service;

import br.com.manoelegidio.tp5.taskmanager.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp5.taskmanager.domain.model.*;
import br.com.manoelegidio.tp5.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp5.taskmanager.producer.TaskEventPublisher;
import br.com.manoelegidio.tp5.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementação refatorada para Arquitetura Orientada a Eventos (EDA).
 * Utiliza RabbitMQ como message broker para comunicação baseada em eventos,
 * eliminando bloqueios de I/O síncronos HTTP e garantindo desacoplamento temporal.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final CategoryService categoryService;
    private final TaskHistoryService historyService;
    private final TaskEventPublisher eventPublisher;

    public TaskServiceImpl(TaskRepository taskRepository,
                           CategoryService categoryService,
                           TaskHistoryService historyService,
                           TaskEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
        this.historyService = historyService;
        this.eventPublisher = eventPublisher;
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

        // Registra histórico de auditoria local (JPA)
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

        // Publica evento de domínio de forma assíncrona no RabbitMQ (ECST Pattern)
        // Desacoplado: resposta HTTP retorna imediatamente sem aguardar o microsserviço de notificações!
        eventPublisher.publishTaskCreated(savedTask, author);

        return TaskResponseDTO.fromEntity(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = findTaskOrThrow(id);
        String author = request.updatedBy() != null ? request.updatedBy() : "Sistema";

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

        TaskStatus oldStatus = task.getStatus();
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
            // Publica evento de alteração de status no RabbitMQ
            eventPublisher.publishTaskStatusChanged(updatedTask, oldStatus, updatedTask.getStatus(), author);
        }

        return TaskResponseDTO.fromEntity(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus status, String updatedBy) {
        Task task = findTaskOrThrow(id);
        if (status != null && task.getStatus() != status) {
            String author = updatedBy != null ? updatedBy : "Sistema";
            TaskStatus oldStatus = task.getStatus();

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

            // Publica evento de transição de status no RabbitMQ
            eventPublisher.publishTaskStatusChanged(task, oldStatus, status, author);
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

        // Publica evento leve de notificação (Event Notification Pattern)
        eventPublisher.publishTaskDeleted(task.getId(), task.getTitle().getValue(), author);

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
    public Map<String, Object> simulateBurstTraffic(int count) {
        int validCount = Math.max(1, Math.min(count, 100));
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= validCount; i++) {
            Task task = new Task(
                    new TaskTitle("Carga de Teste EDA #" + i),
                    "Mensagem em rajada simulando alto tráfego no message broker RabbitMQ.",
                    (i % 5 == 0) ? Priority.URGENT : Priority.MEDIUM,
                    null,
                    null
            );
            eventPublisher.publishTaskCreated(task, "Simulador de Carga EDA");
        }

        long durationMs = System.currentTimeMillis() - startTime;
        log.info("[EDA-SIMULADOR] {} eventos enviados ao RabbitMQ em {} ms", validCount, durationMs);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("eventsPublished", validCount);
        result.put("durationMs", durationMs);
        result.put("averageTimePerEventMs", (double) durationMs / validCount);
        result.put("exchange", "task.direct.exchange e task.topic.exchange");
        result.put("message", "Simulação concluída com sucesso! Os eventos foram enfileirados no RabbitMQ sem bloquear as threads do serviço.");
        return result;
    }

    @Override
    public Map<String, Object> simulatePoisonPill(Long taskId) {
        Long targetId = taskId != null ? taskId : 99999L;
        eventPublisher.publishSimulatedPoisonPill(targetId, "Mensagem Venenosa - Teste de Dead Letter Queue");

        Map<String, Object> result = new HashMap<>();
        result.put("status", "POISON_PILL_DISPATCHED");
        result.put("targetTaskId", targetId);
        result.put("expectedBehavior", "O consumidor tentará processar a mensagem 3 vezes (retry com backoff). Após falhas sucessivas, o RabbitMQ encaminhará a mensagem automaticamente para a Dead Letter Queue (task.dead-letter.queue).");
        result.put("dlqQueue", "task.dead-letter.queue");
        return result;
    }

    @Override
    public Map<String, Object> simulateBroadcastAlert(String message, String actor) {
        String msg = message != null && !message.isBlank() ? message : "Alerta geral de manutenção nos serviços distribuídos.";
        String act = actor != null ? actor : "Administrador";

        eventPublisher.publishBroadcast(msg, act);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "BROADCAST_SENT");
        result.put("message", msg);
        result.put("actor", act);
        result.put("exchange", "task.fanout.exchange");
        result.put("pattern", "Fanout / Publish-Subscribe");
        return result;
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com o ID: " + id));
    }
}
