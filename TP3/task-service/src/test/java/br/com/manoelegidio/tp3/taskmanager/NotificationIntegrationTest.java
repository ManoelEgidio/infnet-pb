package br.com.manoelegidio.tp3.taskmanager;

import br.com.manoelegidio.tp3.taskmanager.client.NotificationClient;
import br.com.manoelegidio.tp3.taskmanager.client.NotificationClientFallback;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationPayloadDTO;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationResponseClientDTO;
import br.com.manoelegidio.tp3.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp3.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp3.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp3.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp3.taskmanager.dto.TaskResponseDTO;
import br.com.manoelegidio.tp3.taskmanager.service.CategoryService;
import br.com.manoelegidio.tp3.taskmanager.service.TaskService;
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
class NotificationIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NotificationClientFallback notificationClientFallback;

    @Autowired
    private NotificationClient notificationClient;

    @Test
    @DisplayName("Deve injetar o cliente Feign NotificationClient e o Fallback")
    void testFeignClientInjected() {
        assertNotNull(notificationClient, "NotificationClient Feign proxy deve ser injetado pelo Spring Cloud");
        assertNotNull(notificationClientFallback, "NotificationClientFallback deve estar presente no contexto");
    }

    @Test
    @DisplayName("Deve executar Fallback com resiliência quando o microsserviço de notificações estiver indisponível")
    void testFallbackExecution() {
        NotificationPayloadDTO payload = new NotificationPayloadDTO(
                99L, "Tarefa Teste Fallback", "admin@infnet.br",
                "Mensagem de teste de resiliência", "HIGH_PRIORITY_ALERT", "IN_APP"
        );

        NotificationResponseClientDTO response = notificationClientFallback.sendNotification(payload);

        assertNotNull(response);
        assertEquals(-1L, response.getId());
        assertTrue(response.getMessage().contains("Fallback"));

        List<NotificationResponseClientDTO> list = notificationClientFallback.getNotificationsByTaskId(99L);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("Deve criar tarefa e disparar notificação distribuída de alta prioridade com resiliência graciosa")
    void testCreateTaskTriggersNotificationGracefully() {
        CategoryDTO category = categoryService.createCategory("DevOps", "Infra e Cloud", "#6366f1");

        TaskRequestDTO request = new TaskRequestDTO(
                "Deploy do Microsserviço de Notificações",
                "Subir serviço na porta 8082 com Spring Boot e Spring Cloud",
                Priority.URGENT,
                TaskStatus.PENDING,
                LocalDateTime.now().plusDays(1),
                category.id(),
                "Manoel Egidio"
        );

        TaskResponseDTO created = taskService.createTask(request);

        assertNotNull(created.id());
        assertEquals("Deploy do Microsserviço de Notificações", created.title());
        assertEquals(Priority.URGENT, created.priority());

        // Atualiza status para COMPLETED gerando segundo disparo de notificação
        TaskResponseDTO completed = taskService.updateTaskStatus(created.id(), TaskStatus.COMPLETED, "Manoel");
        assertEquals(TaskStatus.COMPLETED, completed.status());

        // Consulta notificações associadas à tarefa via endpoint distribuído
        List<NotificationResponseClientDTO> taskNotifications = taskService.getTaskNotifications(created.id());
        assertNotNull(taskNotifications);
    }
}
