package br.com.manoelegidio.tp5.taskmanager.repository;

import br.com.manoelegidio.tp5.taskmanager.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        Category category = new Category("Backend Dev", "Tarefas de API e Banco", "#2563eb");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    @DisplayName("Deve persistir uma tarefa com categoria associada e buscar por ID")
    void testSaveAndFindById() {
        Task task = new Task(
                new TaskTitle("Implementar JPA"),
                "Mapeamento ORM com Spring Data",
                Priority.HIGH,
                LocalDateTime.now().plusDays(2),
                savedCategory
        );

        Task saved = taskRepository.save(task);

        assertNotNull(saved.getId());
        assertEquals("Implementar JPA", saved.getTitle().getValue());
        assertEquals("Backend Dev", saved.getCategory().getName());
        assertEquals(TaskStatus.PENDING, saved.getStatus());
    }

    @Test
    @DisplayName("Deve filtrar tarefas por status, prioridade e categoria via JPQL customizada")
    void testFindByFilters() {
        Task task1 = new Task(new TaskTitle("Tarefa 1"), "Desc 1", Priority.HIGH, LocalDateTime.now(), savedCategory);
        Task task2 = new Task(new TaskTitle("Tarefa 2"), "Desc 2", Priority.LOW, LocalDateTime.now(), savedCategory);
        task2.updateStatus(TaskStatus.COMPLETED);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> pendingHigh = taskRepository.findByFilters(TaskStatus.PENDING, Priority.HIGH, savedCategory.getId());
        assertEquals(1, pendingHigh.size());
        assertEquals("Tarefa 1", pendingHigh.get(0).getTitle().getValue());

        List<Task> completed = taskRepository.findByFilters(TaskStatus.COMPLETED, null, null);
        assertEquals(1, completed.size());
        assertEquals("Tarefa 2", completed.get(0).getTitle().getValue());
    }

    @Test
    @DisplayName("Deve paginar tarefas com sucesso utilizando Pageable")
    void testFindByFiltersPaged() {
        for (int i = 1; i <= 5; i++) {
            taskRepository.save(new Task(new TaskTitle("Tarefa Paginada " + i), "Desc " + i, Priority.MEDIUM, LocalDateTime.now(), savedCategory));
        }

        Page<Task> page = taskRepository.findByFiltersPaged(null, null, savedCategory.getId(), PageRequest.of(0, 3));
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(3, page.getContent().size());
    }

    @Test
    @DisplayName("Deve encontrar tarefas atrasadas corretamente")
    void testFindOverdueTasks() {
        Task overdueTask = new Task(new TaskTitle("Tarefa Atrasada"), "Desc", Priority.URGENT, LocalDateTime.now().minusDays(2), savedCategory);
        Task futureTask = new Task(new TaskTitle("Tarefa Futura"), "Desc", Priority.URGENT, LocalDateTime.now().plusDays(2), savedCategory);

        taskRepository.save(overdueTask);
        taskRepository.save(futureTask);

        List<Task> overdue = taskRepository.findOverdueTasks(LocalDateTime.now());
        assertEquals(1, overdue.size());
        assertEquals("Tarefa Atrasada", overdue.get(0).getTitle().getValue());
    }
}
