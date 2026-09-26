package br.com.manoelegidio.tp4.notification.consumer;

import br.com.manoelegidio.tp4.notification.domain.model.Notification;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp4.notification.event.TaskEventDTO;
import br.com.manoelegidio.tp4.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
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
class TaskEventListenerTest {

    @Autowired
    private TaskEventListener taskEventListener;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        taskEventListener.clearDeadLetterHistory();
    }

    @Test
    @DisplayName("Deve consumir evento de ciclo de vida (Direct Exchange) e persistir notificação")
    void testHandleLifecycleEvent() {
        TaskEventDTO event = new TaskEventDTO();
        event.setEventId("EVT-001");
        event.setEventType("TASK_CREATED");
        event.setTaskId(101L);
        event.setTitle("Modelar Fila RabbitMQ");
        event.setDescription("Criar bindings entre DirectExchange e Queue");
        event.setStatus("PENDING");
        event.setPriority("MEDIUM");
        event.setCategoryName("Backend");
        event.setActor("Manoel Egidio");
        event.setTimestamp(LocalDateTime.now());

        taskEventListener.handleTaskLifecycleEvent(event);

        List<Notification> notifications = notificationRepository.findByTaskIdOrderByCreatedAtDesc(101L);
        assertFalse(notifications.isEmpty(), "Deve persistir notificação para a tarefa 101");
        Notification saved = notifications.get(0);
        assertEquals("Modelar Fila RabbitMQ", saved.getTaskTitle());
        assertEquals(NotificationType.TASK_CREATED, saved.getType());
        assertFalse(saved.isRead());
    }

    @Test
    @DisplayName("Deve consumir evento prioritário (Topic Exchange) e registrar alerta crítico")
    void testHandleUrgentTopicEvent() {
        TaskEventDTO event = new TaskEventDTO();
        event.setEventId("EVT-URGENT-001");
        event.setEventType("TASK_CREATED");
        event.setTaskId(202L);
        event.setTitle("Servidor em Chamas");
        event.setPriority("URGENT");
        event.setActor("DevOps");

        taskEventListener.handleUrgentTopicEvent(event);

        List<Notification> urgentList = notificationRepository.findByTaskIdOrderByCreatedAtDesc(202L);
        assertFalse(urgentList.isEmpty());
        Notification notification = urgentList.get(0);
        assertEquals(NotificationType.HIGH_PRIORITY_ALERT, notification.getType());
        assertTrue(notification.getMessage().contains("ALERTA CRÍTICO"));
    }

    @Test
    @DisplayName("Deve consumir evento de Broadcast (Fanout Exchange)")
    void testHandleBroadcastEvent() {
        TaskEventDTO event = new TaskEventDTO();
        event.setEventId("EVT-FANOUT-001");
        event.setEventType("SYSTEM_BROADCAST");
        event.setDescription("Aviso de manutenção emergencial no RabbitMQ cluster");
        event.setActor("Admin");

        taskEventListener.handleBroadcastEvent(event);

        List<Notification> all = notificationRepository.findAll();
        boolean foundBroadcast = all.stream()
                .anyMatch(n -> n.getType() == NotificationType.SYSTEM_ALERT &&
                        n.getMessage().contains("manutenção emergencial"));
        assertTrue(foundBroadcast, "Notificação de broadcast geral deve ter sido gravada");
    }

    @Test
    @DisplayName("Deve rejeitar mensagem venenosa lançando AmqpRejectAndDontRequeueException para rotear à DLQ")
    void testPoisonPillRejectionForDLQ() {
        TaskEventDTO poisonEvent = new TaskEventDTO();
        poisonEvent.setEventId("EVT-POISON-001");
        poisonEvent.setTaskId(999L);
        poisonEvent.setTitle("Mensagem com erro simulado");
        poisonEvent.setSimulateError(true);

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> {
            taskEventListener.handleTaskLifecycleEvent(poisonEvent);
        }, "Consumidor deve lançar AmqpRejectAndDontRequeueException para acionar a Dead Letter Queue");
    }

    @Test
    @DisplayName("Deve capturar e armazenar mensagem da Dead Letter Queue para observabilidade")
    void testDeadLetterMessageCapture() {
        TaskEventDTO failedEvent = new TaskEventDTO();
        failedEvent.setEventId("EVT-DLQ-RECORD");
        failedEvent.setTaskId(555L);
        failedEvent.setTitle("Falha crítica de processamento");
        failedEvent.setSimulateError(true);

        taskEventListener.handleDeadLetterMessage(failedEvent);

        List<TaskEventDTO> history = taskEventListener.getDeadLetterHistory();
        assertEquals(1, history.size());
        assertEquals("EVT-DLQ-RECORD", history.get(0).getEventId());
        assertEquals(555L, history.get(0).getTaskId());
    }
}
