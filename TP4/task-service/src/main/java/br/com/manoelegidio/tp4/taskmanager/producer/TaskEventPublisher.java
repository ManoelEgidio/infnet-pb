package br.com.manoelegidio.tp4.taskmanager.producer;

import br.com.manoelegidio.tp4.taskmanager.config.RabbitMQConfig;
import br.com.manoelegidio.tp4.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp4.taskmanager.domain.model.Task;
import br.com.manoelegidio.tp4.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp4.taskmanager.event.TaskEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos de domínio no RabbitMQ.
 * Implementa envio assíncrono não-bloqueante, desacoplando o task-service
 * do ciclo de vida e disponibilidade dos consumidores.
 */
@Component
public class TaskEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TaskEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public TaskEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Padrão Event-Carried State Transfer (ECST) para criação de tarefa.
     */
    public void publishTaskCreated(Task task, String actor) {
        TaskEventDTO event = new TaskEventDTO(
                "TASK_CREATED",
                task.getId(),
                task.getTitle().getValue(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getCategory() != null ? task.getCategory().getName() : "Sem Categoria",
                task.getDueDate() != null ? task.getDueDate().toString() : null,
                actor
        );

        log.info("[EDA] Publicando evento TASK_CREATED no DirectExchange: {}", event.getEventId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_CREATED, event);

        // Se for tarefa urgente, envia também para a TopicExchange para roteamento prioritário
        if (task.getPriority() == Priority.URGENT || task.getPriority() == Priority.HIGH) {
            log.info("[EDA] Tarefa de alta prioridade. Publicando no TopicExchange: task.priority.URGENT");
            rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, "task.priority.URGENT", event);
        }
    }

    /**
     * Padrão Event-Carried State Transfer (ECST) para atualização de status.
     */
    public void publishTaskStatusChanged(Task task, TaskStatus oldStatus, TaskStatus newStatus, String actor) {
        String eventType = (newStatus == TaskStatus.COMPLETED) ? "TASK_COMPLETED" : "TASK_STATUS_CHANGED";
        String routingKey = (newStatus == TaskStatus.COMPLETED) ? RabbitMQConfig.ROUTING_KEY_COMPLETED : RabbitMQConfig.ROUTING_KEY_STATUS;

        TaskEventDTO event = new TaskEventDTO(
                eventType,
                task.getId(),
                task.getTitle().getValue(),
                task.getDescription(),
                newStatus.name(),
                task.getPriority().name(),
                task.getCategory() != null ? task.getCategory().getName() : "Sem Categoria",
                task.getDueDate() != null ? task.getDueDate().toString() : null,
                actor
        );

        log.info("[EDA] Publicando evento {} no DirectExchange com chave {}: {}", eventType, routingKey, event.getEventId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, routingKey, event);
    }

    /**
     * Padrão Event Notification: Notificação leve de exclusão (apenas IDs e timestamps).
     */
    public void publishTaskDeleted(Long taskId, String title, String actor) {
        TaskEventDTO event = new TaskEventDTO();
        event.setEventType("TASK_DELETED");
        event.setTaskId(taskId);
        event.setTitle(title);
        event.setActor(actor);

        log.info("[EDA] Publicando evento leve TASK_DELETED no DirectExchange: {}", event.getEventId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_DELETED, event);
    }

    /**
     * Padrão Fanout Exchange: Broadcast de mensagens e alertas para múltiplos consumidores.
     */
    public void publishBroadcast(String message, String actor) {
        TaskEventDTO event = new TaskEventDTO();
        event.setEventType("SYSTEM_BROADCAST");
        event.setTitle("Alerta de Sistema (Broadcast)");
        event.setDescription(message);
        event.setActor(actor);

        log.info("[EDA] Disparando Broadcast no FanoutExchange: {}", event.getEventId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);
    }

    /**
     * Simulação de Mensagem Venenosa (Poison Pill) para teste da Dead Letter Queue (DLQ).
     */
    public void publishSimulatedPoisonPill(Long taskId, String title) {
        TaskEventDTO poisonPill = new TaskEventDTO();
        poisonPill.setEventType("POISON_PILL_TEST");
        poisonPill.setTaskId(taskId);
        poisonPill.setTitle(title != null ? title : "Tarefa Venenosa para Teste de DLQ");
        poisonPill.setDescription("Esta mensagem contém parâmetros inválidos para forçar retentativas e encaminhamento à DLQ.");
        poisonPill.setSimulateError(true);

        log.warn("[EDA-SIMULADOR] Publicando mensagem venenosa intencional para teste da DLQ: {}", poisonPill.getEventId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_CREATED, poisonPill);
    }
}
