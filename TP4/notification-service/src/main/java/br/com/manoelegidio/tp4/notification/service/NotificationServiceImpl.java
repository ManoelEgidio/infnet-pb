package br.com.manoelegidio.tp4.notification.service;

import br.com.manoelegidio.tp4.notification.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp4.notification.domain.exception.ValidationException;
import br.com.manoelegidio.tp4.notification.domain.model.Notification;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp4.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationResponseDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationSummaryDTO;
import br.com.manoelegidio.tp4.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Os dados da notificação não podem ser nulos.");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new ValidationException("A mensagem da notificação é obrigatória.");
        }

        String recipient = (dto.getRecipient() != null && !dto.getRecipient().isBlank())
                ? dto.getRecipient()
                : "manoel@infnet.edu.br";

        NotificationType type = dto.getType() != null ? dto.getType() : NotificationType.SYSTEM_ALERT;
        NotificationChannel channel = dto.getChannel() != null ? dto.getChannel() : NotificationChannel.IN_APP;

        Notification notification = new Notification(
                dto.getTaskId(),
                dto.getTaskTitle(),
                recipient,
                dto.getMessage(),
                type,
                channel
        );

        Notification saved = notificationRepository.save(notification);
        log.info("Notificação registrada com sucesso: ID={}, Tipo={}, Destinatário={}, Tarefa={}",
                saved.getId(), saved.getType(), saved.getRecipient(), saved.getTaskId());

        return NotificationResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAllNotifications(String recipient, Boolean unreadOnly) {
        List<Notification> list;

        boolean filterByRecipient = recipient != null && !recipient.isBlank();
        boolean filterUnread = Boolean.TRUE.equals(unreadOnly);

        if (filterByRecipient && filterUnread) {
            list = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient);
        } else if (filterByRecipient) {
            list = notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
        } else if (filterUnread) {
            list = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        } else {
            list = notificationRepository.findAllByOrderByCreatedAtDesc();
        }

        return list.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com ID: " + id));
        return NotificationResponseDTO.fromEntity(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByTaskId(Long taskId) {
        return notificationRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada com ID: " + id));

        notification.markAsRead();
        Notification updated = notificationRepository.save(notification);
        log.info("Notificação marcada como lida: ID={}", id);
        return NotificationResponseDTO.fromEntity(updated);
    }

    @Override
    public int markAllAsRead(String recipient) {
        int updatedCount;
        if (recipient != null && !recipient.isBlank()) {
            updatedCount = notificationRepository.markAllAsReadByRecipient(recipient);
            log.info("Todas as notificações pendentes para o destinatário '{}' foram marcadas como lidas (Total: {})",
                    recipient, updatedCount);
        } else {
            updatedCount = notificationRepository.markAllAsRead();
            log.info("Todas as notificações pendentes foram marcadas como lidas (Total: {})", updatedCount);
        }
        return updatedCount;
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notificação não encontrada com ID: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("Notificação removida: ID={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationSummaryDTO getSummary(String recipient) {
        boolean filterByRecipient = recipient != null && !recipient.isBlank();
        long total;
        long unread;

        if (filterByRecipient) {
            total = notificationRepository.countByRecipient(recipient);
            unread = notificationRepository.countByRecipientAndIsReadFalse(recipient);
        } else {
            total = notificationRepository.count();
            unread = notificationRepository.countByIsReadFalse();
        }

        long read = total - unread;
        return new NotificationSummaryDTO(total, unread, read, recipient);
    }
}
