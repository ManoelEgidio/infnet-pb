package br.com.manoelegidio.tp4.notification.repository;

import br.com.manoelegidio.tp4.notification.domain.model.Notification;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
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
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar uma notificação com sucesso e gerar ID")
    void shouldSaveNotificationSuccessfully() {
        Notification notification = new Notification(
                10L,
                "Implementar Spring Cloud Feign",
                "manoel@infnet.edu.br",
                "Tarefa de alta prioridade cadastrada",
                NotificationType.HIGH_PRIORITY_ALERT,
                NotificationChannel.IN_APP
        );

        Notification saved = notificationRepository.save(notification);

        assertNotNull(saved.getId());
        assertEquals("Implementar Spring Cloud Feign", saved.getTaskTitle());
        assertFalse(saved.isRead());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Deve filtrar apenas notificações não lidas e ordenar por data decrescente")
    void shouldFindUnreadNotificationsOrderedByDate() {
        Notification n1 = new Notification(1L, "T1", "manoel@infnet.edu.br", "Msg 1",
                NotificationType.TASK_CREATED, NotificationChannel.IN_APP);
        Notification n2 = new Notification(2L, "T2", "manoel@infnet.edu.br", "Msg 2",
                NotificationType.STATUS_CHANGED, NotificationChannel.IN_APP);
        n2.markAsRead();

        notificationRepository.save(n1);
        notificationRepository.save(n2);

        List<Notification> unreadList = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();

        assertEquals(1, unreadList.size());
        assertEquals("Msg 1", unreadList.get(0).getMessage());
        assertEquals(1, notificationRepository.countByIsReadFalse());
    }

    @Test
    @DisplayName("Deve buscar notificações atreladas a uma tarefa específica")
    void shouldFindNotificationsByTaskId() {
        Notification n1 = new Notification(99L, "Tarefa 99", "manoel@infnet.edu.br", "Evento 1",
                NotificationType.TASK_CREATED, NotificationChannel.IN_APP);
        Notification n2 = new Notification(99L, "Tarefa 99", "manoel@infnet.edu.br", "Evento 2",
                NotificationType.TASK_COMPLETED, NotificationChannel.IN_APP);
        Notification n3 = new Notification(100L, "Tarefa 100", "manoel@infnet.edu.br", "Outro",
                NotificationType.TASK_CREATED, NotificationChannel.IN_APP);

        notificationRepository.saveAll(List.of(n1, n2, n3));

        List<Notification> list = notificationRepository.findByTaskIdOrderByCreatedAtDesc(99L);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Deve marcar todas as notificações como lidas em lote")
    void shouldMarkAllAsReadInBatch() {
        Notification n1 = new Notification(1L, "T1", "user@infnet.br", "Msg 1",
                NotificationType.TASK_CREATED, NotificationChannel.IN_APP);
        Notification n2 = new Notification(2L, "T2", "user@infnet.br", "Msg 2",
                NotificationType.STATUS_CHANGED, NotificationChannel.IN_APP);

        notificationRepository.save(n1);
        notificationRepository.save(n2);

        assertEquals(2, notificationRepository.countByIsReadFalse());

        int updated = notificationRepository.markAllAsRead();
        assertEquals(2, updated);
        assertEquals(0, notificationRepository.countByIsReadFalse());
    }
}
