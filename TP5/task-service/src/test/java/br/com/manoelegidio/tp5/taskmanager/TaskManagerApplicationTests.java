package br.com.manoelegidio.tp5.taskmanager;

import br.com.manoelegidio.tp5.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp5.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskHistoryDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp5.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp5.taskmanager.service.CategoryService;
import br.com.manoelegidio.tp5.taskmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskManagerApplicationTests {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void contextLoads() {
        assertNotNull(taskService);
        assertNotNull(categoryService);
    }

    @Test
    @DisplayName("Deve criar tarefa, alterar status e registrar histórico completo de auditoria")
    void testCreateTaskAndHistoryTracking() {
        CategoryDTO category = categoryService.createCategory("Teste Integrado", "Categoria de teste", "#10b981");

        TaskRequestDTO request = new TaskRequestDTO(
                "Criar Persistência Real",
                "Integrar Spring Data JPA com Histórico",
                Priority.HIGH,
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(3),
                category.id(),
                "Manoel Egidio"
        );

        TaskResponseDTO created = taskService.createTask(request);
        assertNotNull(created.id());
        assertEquals("Criar Persistência Real", created.title());
        assertEquals(TaskStatus.PENDING, created.status());
        assertEquals("Teste Integrado", created.category().name());

        // Altera status para gerar histórico
        taskService.updateTaskStatus(created.id(), TaskStatus.IN_PROGRESS, "Manoel QA");

        List<TaskHistoryDTO> history = taskService.getTaskHistory(created.id());
        assertFalse(history.isEmpty());
        assertTrue(history.size() >= 2); // 1 de criação + 1 de alteração de status
    }
}
