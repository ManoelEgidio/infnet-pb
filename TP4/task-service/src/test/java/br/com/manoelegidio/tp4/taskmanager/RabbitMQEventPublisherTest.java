package br.com.manoelegidio.tp4.taskmanager;

import br.com.manoelegidio.tp4.taskmanager.config.RabbitMQConfig;
import br.com.manoelegidio.tp4.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp4.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp4.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp4.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp4.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp4.taskmanager.producer.TaskEventPublisher;
import br.com.manoelegidio.tp4.taskmanager.service.CategoryService;
import br.com.manoelegidio.tp4.taskmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RabbitMQEventPublisherTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TaskEventPublisher eventPublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Deve injetar o publicador TaskEventPublisher e RabbitTemplate no contexto Spring")
    void testBeansInjected() {
        assertNotNull(eventPublisher, "TaskEventPublisher deve estar presente no contexto");
        assertNotNull(rabbitTemplate, "RabbitTemplate deve estar configurado com Jackson2JsonMessageConverter");
    }

    @Test
    @DisplayName("Deve criar tarefa e publicar evento de domínio assíncrono no RabbitMQ sem bloqueio")
    void testCreateTaskPublishesEvent() {
        CategoryDTO category = categoryService.createCategory("DevOps", "Infra e Cloud", "#6366f1");

        TaskRequestDTO request = new TaskRequestDTO(
                "Configurar Cluster RabbitMQ",
                "Subir exchanges Direct, Topic e Fanout",
                Priority.URGENT,
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(2),
                category.id(),
                "Manoel Egidio"
        );

        TaskResponseDTO created = taskService.createTask(request);

        assertNotNull(created.id());
        assertEquals("Configurar Cluster RabbitMQ", created.title());
        assertEquals(Priority.URGENT, created.priority());

        // Atualização de status emite novo evento assíncrono
        TaskResponseDTO completed = taskService.updateTaskStatus(created.id(), TaskStatus.COMPLETED, "Manoel");
        assertEquals(TaskStatus.COMPLETED, completed.status());
    }

    @Test
    @DisplayName("Deve simular rajada de tráfego (Burst Traffic) publicando múltiplos eventos no RabbitMQ")
    void testSimulateBurstTraffic() {
        Map<String, Object> result = taskService.simulateBurstTraffic(10);

        assertNotNull(result);
        assertEquals("SUCCESS", result.get("status"));
        assertEquals(10, result.get("eventsPublished"));
        assertTrue((Long) result.get("durationMs") >= 0);
    }

    @Test
    @DisplayName("Deve simular publicação de mensagem venenosa (Poison Pill) para Dead Letter Queue")
    void testSimulatePoisonPill() {
        Map<String, Object> result = taskService.simulatePoisonPill(12345L);

        assertNotNull(result);
        assertEquals("POISON_PILL_DISPATCHED", result.get("status"));
        assertEquals(12345L, result.get("targetTaskId"));
        assertEquals(RabbitMQConfig.DEAD_LETTER_QUEUE, result.get("dlqQueue"));
    }

    @Test
    @DisplayName("Deve publicar alerta geral de broadcast no FanoutExchange")
    void testSimulateBroadcast() {
        Map<String, Object> result = taskService.simulateBroadcastAlert("Manutenção programada às 22h", "DevOps Admin");

        assertNotNull(result);
        assertEquals("BROADCAST_SENT", result.get("status"));
        assertEquals(RabbitMQConfig.FANOUT_EXCHANGE, result.get("exchange"));
    }
}
