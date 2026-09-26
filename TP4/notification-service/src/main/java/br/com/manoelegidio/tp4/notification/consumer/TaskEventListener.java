package br.com.manoelegidio.tp4.notification.consumer;

import br.com.manoelegidio.tp4.notification.config.RabbitMQConsumerConfig;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp4.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp4.notification.event.TaskEventDTO;
import br.com.manoelegidio.tp4.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Consumidor de eventos RabbitMQ (@RabbitListener).
 * Processa mensagens assíncronas enviadas pelo task-service:
 * - Direct Queue: Ciclo de vida da tarefa
 * - Topic Queue: Tarefas urgentes e de alta prioridade
 * - Fanout Queue: Broadcasts para todos os serviços
 * - Dead Letter Queue: Captura de mensagens venenosas e rejeitadas
 */
@Component
public class TaskEventListener {

    private static final Logger log = LoggerFactory.getLogger(TaskEventListener.class);

    private final NotificationService notificationService;
    private final List<TaskEventDTO> deadLetterHistory = Collections.synchronizedList(new ArrayList<>());

    public TaskEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 1. Consumo do Ciclo de Vida de Tarefas (Direct Exchange)
     */
    @RabbitListener(queues = RabbitMQConsumerConfig.NOTIFICATION_QUEUE)
    public void handleTaskLifecycleEvent(TaskEventDTO event) {
        log.info("[EDA-CONSUMIDOR] Mensagem recebida na fila {}: eventId={}, tipo={}, taskId={}",
                RabbitMQConsumerConfig.NOTIFICATION_QUEUE, event.getEventId(), event.getEventType(), event.getTaskId());

        // Simulação de Poison Pill / Mensagem Venenosa (Rubrica TP4)
        if (event.isSimulateError()) {
            log.error("[EDA-SIMULADOR] Simulação de falha ativada para a mensagem {}. Lançando AmqpRejectAndDontRequeueException...", event.getEventId());
            throw new AmqpRejectAndDontRequeueException("Falha intencional de simulação: rejeitando mensagem para teste de Dead Letter Queue (DLQ)!");
        }

        NotificationType type = mapEventType(event.getEventType(), event.getPriority());
        String message = buildNotificationMessage(event);

        NotificationRequestDTO requestDTO = new NotificationRequestDTO(
                event.getTaskId(),
                event.getTitle(),
                event.getActor() != null ? event.getActor() : "manoel@infnet.edu.br",
                message,
                type,
                NotificationChannel.IN_APP
        );

        notificationService.createNotification(requestDTO);
        log.info("[EDA-CONSUMIDOR] Notificação persistida com sucesso para o evento {}", event.getEventId());
    }

    /**
     * 2. Consumo de Tópicos Prioritários (Topic Exchange - task.priority.URGENT)
     */
    @RabbitListener(queues = RabbitMQConsumerConfig.URGENT_QUEUE)
    public void handleUrgentTopicEvent(TaskEventDTO event) {
        log.warn("[EDA-TOPIC-ALERTA] Evento prioritário detectado via TopicExchange: taskId={}, title='{}'",
                event.getTaskId(), event.getTitle());

        NotificationRequestDTO urgentNotification = new NotificationRequestDTO(
                event.getTaskId(),
                event.getTitle(),
                "alerta-urgente@infnet.edu.br",
                "ALERTA CRÍTICO (Topic Exchange): Tarefa urgente requer atenção imediata: '" + event.getTitle() + "'.",
                NotificationType.HIGH_PRIORITY_ALERT,
                NotificationChannel.EMAIL
        );

        notificationService.createNotification(urgentNotification);
    }

    /**
     * 3. Consumo de Broadcasts do Sistema (Fanout Exchange)
     */
    @RabbitListener(queues = RabbitMQConsumerConfig.BROADCAST_QUEUE)
    public void handleBroadcastEvent(TaskEventDTO event) {
        log.info("[EDA-FANOUT-BROADCAST] Broadcast recebido via FanoutExchange: {}", event.getDescription());

        NotificationRequestDTO broadcastNotification = new NotificationRequestDTO(
                null,
                "Sistema (Broadcast Geral)",
                "todos-usuarios@infnet.edu.br",
                event.getDescription() != null ? event.getDescription() : "Alerta geral emitido pelo sistema.",
                NotificationType.SYSTEM_ALERT,
                NotificationChannel.IN_APP
        );

        notificationService.createNotification(broadcastNotification);
    }

    /**
     * 4. Consumo da Dead Letter Queue (DLQ) para Auditoria e Observabilidade
     */
    @RabbitListener(queues = RabbitMQConsumerConfig.DEAD_LETTER_QUEUE)
    public void handleDeadLetterMessage(TaskEventDTO failedEvent) {
        log.error("[EDA-DLQ-AUDITORIA] Mensagem venenosa capturada na Dead Letter Queue! eventId={}, taskId={}, erroSimulado={}",
                failedEvent.getEventId(), failedEvent.getTaskId(), failedEvent.isSimulateError());

        deadLetterHistory.add(failedEvent);
    }

    public List<TaskEventDTO> getDeadLetterHistory() {
        return new ArrayList<>(deadLetterHistory);
    }

    public void clearDeadLetterHistory() {
        deadLetterHistory.clear();
    }

    private NotificationType mapEventType(String eventType, String priority) {
        if ("URGENT".equalsIgnoreCase(priority) || "HIGH".equalsIgnoreCase(priority)) {
            return NotificationType.HIGH_PRIORITY_ALERT;
        }
        if ("TASK_COMPLETED".equalsIgnoreCase(eventType)) {
            return NotificationType.TASK_COMPLETED;
        }
        if ("TASK_STATUS_CHANGED".equalsIgnoreCase(eventType)) {
            return NotificationType.STATUS_CHANGED;
        }
        if ("TASK_DELETED".equalsIgnoreCase(eventType)) {
            return NotificationType.SYSTEM_ALERT;
        }
        return NotificationType.TASK_CREATED;
    }

    private String buildNotificationMessage(TaskEventDTO event) {
        if ("TASK_DELETED".equalsIgnoreCase(event.getEventType())) {
            return "A tarefa #" + event.getTaskId() + " ('" + event.getTitle() + "') foi removida por " + event.getActor() + ".";
        }
        if ("TASK_COMPLETED".equalsIgnoreCase(event.getEventType())) {
            return "Parabéns! A tarefa '" + event.getTitle() + "' foi marcada como concluída por " + event.getActor() + ".";
        }
        if ("TASK_STATUS_CHANGED".equalsIgnoreCase(event.getEventType())) {
            return "Status da tarefa '" + event.getTitle() + "' alterado para " + event.getStatus() + " por " + event.getActor() + ".";
        }
        return "Nova tarefa registrada no sistema: '" + event.getTitle() + "' (Categoria: " + event.getCategoryName() + ").";
    }
}
