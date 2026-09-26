package br.com.manoelegidio.tp5.notification.controller;

import br.com.manoelegidio.tp5.notification.consumer.TaskEventListener;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp5.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp5.notification.dto.NotificationResponseDTO;
import br.com.manoelegidio.tp5.notification.dto.NotificationSummaryDTO;
import br.com.manoelegidio.tp5.notification.event.TaskEventDTO;
import br.com.manoelegidio.tp5.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class NotificationController {

    private final NotificationService notificationService;
    private final TaskEventListener eventListener;

    public NotificationController(NotificationService notificationService, TaskEventListener eventListener) {
        this.notificationService = notificationService;
        this.eventListener = eventListener;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@Valid @RequestBody NotificationRequestDTO dto) {
        NotificationResponseDTO created = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAll(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) Boolean unreadOnly) {
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications(recipient, unreadOnly);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getById(@PathVariable Long id) {
        NotificationResponseDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByTaskId(@PathVariable Long taskId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByTaskId(taskId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/summary")
    public ResponseEntity<NotificationSummaryDTO> getSummary(@RequestParam(required = false) String recipient) {
        NotificationSummaryDTO summary = notificationService.getSummary(recipient);
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        NotificationResponseDTO updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestParam(required = false) String recipient) {
        int updatedCount = notificationService.markAllAsRead(recipient);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notificações marcadas como lidas.");
        response.put("updatedCount", updatedCount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/broadcast")
    public ResponseEntity<NotificationResponseDTO> broadcastAlert(@RequestParam String message) {
        NotificationRequestDTO dto = new NotificationRequestDTO(
                null,
                "Alerta Geral do Sistema",
                "todos@infnet.edu.br",
                message,
                NotificationType.SYSTEM_ALERT,
                NotificationChannel.IN_APP
        );
        NotificationResponseDTO created = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================================================================
    // Endpoints EDA e Dead Letter Queue (DLQ) para Observabilidade e Simulação
    // =========================================================================

    @GetMapping("/dlq")
    public ResponseEntity<List<TaskEventDTO>> getDeadLetterMessages() {
        return ResponseEntity.ok(eventListener.getDeadLetterHistory());
    }

    @DeleteMapping("/dlq")
    public ResponseEntity<Map<String, String>> clearDeadLetterMessages() {
        eventListener.clearDeadLetterHistory();
        Map<String, String> res = new HashMap<>();
        res.put("status", "CLEARED");
        res.put("message", "Histórico de mensagens da Dead Letter Queue limpo com sucesso.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/eda/status")
    public ResponseEntity<Map<String, Object>> getEdaStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("broker", "RabbitMQ 3.13.7");
        status.put("consumerStatus", "ACTIVE");
        status.put("queues", List.of("task.notification.queue", "task.urgent.queue", "task.broadcast.queue", "task.dead-letter.queue"));
        status.put("dlqCount", eventListener.getDeadLetterHistory().size());
        status.put("patterns", List.of("Event-Carried State Transfer", "Event Notification", "Dead Letter Exchange"));
        return ResponseEntity.ok(status);
    }
}
