package br.com.manoelegidio.tp4.notification.service;

import br.com.manoelegidio.tp4.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationResponseDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationSummaryDTO;

import java.util.List;

public interface NotificationService {

    NotificationResponseDTO createNotification(NotificationRequestDTO dto);

    List<NotificationResponseDTO> getAllNotifications(String recipient, Boolean unreadOnly);

    NotificationResponseDTO getNotificationById(Long id);

    List<NotificationResponseDTO> getNotificationsByTaskId(Long taskId);

    NotificationResponseDTO markAsRead(Long id);

    int markAllAsRead(String recipient);

    void deleteNotification(Long id);

    NotificationSummaryDTO getSummary(String recipient);
}
