package br.com.manoelegidio.tp1.taskmanager;

import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp1.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskManagerApplicationTests {

    @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() {
        assertNotNull(taskService);
    }

    @Test
    void testCreateTaskAndRetrieveMetrics() {
        TaskRequestDTO request = new TaskRequestDTO(
                "Test Unit Task",
                "Testing Spring Boot Monolith Service Layer",
                Priority.HIGH,
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(1)
        );

        TaskResponseDTO created = taskService.createTask(request);

        assertNotNull(created.id());
        assertEquals("Test Unit Task", created.title());
        assertEquals(Priority.HIGH, created.priority());
        assertEquals(TaskStatus.PENDING, created.status());
    }
}
