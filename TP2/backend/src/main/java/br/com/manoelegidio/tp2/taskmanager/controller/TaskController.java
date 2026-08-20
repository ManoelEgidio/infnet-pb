package br.com.manoelegidio.tp2.taskmanager.controller;

import br.com.manoelegidio.tp2.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp2.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp2.taskmanager.dto.DashboardMetricsDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp2.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp2.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
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
}
