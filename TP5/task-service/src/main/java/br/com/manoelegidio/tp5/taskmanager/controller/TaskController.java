package br.com.manoelegidio.tp5.taskmanager.controller;

import br.com.manoelegidio.tp5.taskmanager.config.RabbitMQConfig;
import br.com.manoelegidio.tp5.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp5.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp5.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long categoryId) {
        List<TaskResponseDTO> tasks = taskService.getAllTasks(status, priority, categoryId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        TaskResponseDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(@PathVariable Long id) {
        List<TaskHistoryDTO> history = taskService.getTaskHistory(id);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO updated = taskService.updateTask(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @RequestParam(required = false, defaultValue = "Usuário") String updatedBy) {
        TaskResponseDTO updated = taskService.updateTaskStatus(id, status, updatedBy);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Usuário") String deletedBy) {
        taskService.deleteTask(id, deletedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        DashboardMetricsDTO metrics = taskService.getMetrics();
        return ResponseEntity.ok(metrics);
    }

    // =========================================================================
    // Endpoints de Demonstração e Simulação EDA com RabbitMQ (Rubrica TP5)
    // =========================================================================

    @PostMapping("/simulation/burst")
    public ResponseEntity<Map<String, Object>> simulateBurstTraffic(
            @RequestParam(defaultValue = "15") int count) {
        Map<String, Object> result = taskService.simulateBurstTraffic(count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/simulation/poison-pill")
    public ResponseEntity<Map<String, Object>> simulatePoisonPill(
            @RequestParam(required = false, defaultValue = "99999") Long taskId) {
        Map<String, Object> result = taskService.simulatePoisonPill(taskId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/simulation/broadcast")
    public ResponseEntity<Map<String, Object>> simulateBroadcast(
            @RequestBody(required = false) Map<String, String> body) {
        String message = (body != null && body.containsKey("message")) ? body.get("message") : "Atenção: Simulação de Broadcast em tempo real.";
        String actor = (body != null && body.containsKey("actor")) ? body.get("actor") : "Painel EDA";
        Map<String, Object> result = taskService.simulateBroadcastAlert(message, actor);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/simulation/topology")
    public ResponseEntity<Map<String, Object>> getTopology() {
        Map<String, Object> topology = new HashMap<>();
        topology.put("directExchange", RabbitMQConfig.DIRECT_EXCHANGE);
        topology.put("topicExchange", RabbitMQConfig.TOPIC_EXCHANGE);
        topology.put("fanoutExchange", RabbitMQConfig.FANOUT_EXCHANGE);
        topology.put("dlxExchange", RabbitMQConfig.DLX_EXCHANGE);
        topology.put("notificationQueue", RabbitMQConfig.NOTIFICATION_QUEUE);
        topology.put("urgentQueue", RabbitMQConfig.URGENT_QUEUE);
        topology.put("broadcastQueue", RabbitMQConfig.BROADCAST_QUEUE);
        topology.put("deadLetterQueue", RabbitMQConfig.DEAD_LETTER_QUEUE);
        topology.put("brokerUrl", "amqp://guest:guest@localhost:5672");
        topology.put("managementUi", "http://localhost:15672");
        return ResponseEntity.ok(topology);
    }
}
