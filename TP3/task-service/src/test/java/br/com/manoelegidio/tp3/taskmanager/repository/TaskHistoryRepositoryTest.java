package br.com.manoelegidio.tp3.taskmanager.repository;

import br.com.manoelegidio.tp3.taskmanager.domain.model.ActionType;
import br.com.manoelegidio.tp3.taskmanager.domain.model.TaskHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskHistoryRepositoryTest {

    @Autowired
    private TaskHistoryRepository historyRepository;

    @Test
    @DisplayName("Deve registrar e consultar histórico de alterações ordenado cronologicamente")
    void testSaveAndFindHistory() {
        TaskHistory h1 = new TaskHistory(
                100L,
                ActionType.CREATED,
                "TASK",
                null,
                "Nova Tarefa",
                "Tarefa criada",
                "Manoel"
        );

        TaskHistory h2 = new TaskHistory(
                100L,
                ActionType.STATUS_CHANGED,
                "status",
                "PENDING",
                "IN_PROGRESS",
                "Iniciado o desenvolvimento",
                "Manoel"
        );

        historyRepository.save(h1);
        historyRepository.save(h2);

        List<TaskHistory> historyList = historyRepository.findByTaskIdOrderByChangedAtDesc(100L);
        assertEquals(2, historyList.size());
        assertEquals(2, historyRepository.countByTaskId(100L));
    }

    @Test
    @DisplayName("Deve buscar histórico de um campo específico via JPQL")
    void testFindFieldHistory() {
        TaskHistory h1 = new TaskHistory(200L, ActionType.STATUS_CHANGED, "status", "PENDING", "IN_PROGRESS", "update status", "Manoel");
        TaskHistory h2 = new TaskHistory(200L, ActionType.DETAILS_UPDATED, "title", "A", "B", "update title", "Manoel");

        historyRepository.save(h1);
        historyRepository.save(h2);

        List<TaskHistory> statusHistory = historyRepository.findFieldHistoryByTaskId(200L, "status");
        assertEquals(1, statusHistory.size());
        assertEquals("PENDING", statusHistory.get(0).getOldValue());
        assertEquals("IN_PROGRESS", statusHistory.get(0).getNewValue());
    }
}
