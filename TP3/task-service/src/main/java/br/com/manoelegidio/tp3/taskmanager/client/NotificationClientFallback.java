package br.com.manoelegidio.tp3.taskmanager.client;

import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationPayloadDTO;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationResponseClientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public NotificationResponseClientDTO sendNotification(NotificationPayloadDTO request) {
        log.warn("Circuito Aberto / Fallback Ativo: Não foi possível alcançar o microsserviço de notificações na porta 8082. Operação continuará com degradação graciosa. Mensagem: {}",
                request != null ? request.getMessage() : "null");
        NotificationResponseClientDTO fallback = new NotificationResponseClientDTO();
        fallback.setId(-1L);
        fallback.setMessage("Notificação enfileirada localmente (Modo Degradado / Fallback).");
        return fallback;
    }

    @Override
    public List<NotificationResponseClientDTO> getNotificationsByTaskId(Long taskId) {
        log.warn("Circuito Aberto / Fallback Ativo: Falha ao consultar notificações para taskId={}", taskId);
        return Collections.emptyList();
    }
}
