package br.com.manoelegidio.tp4.notification.service;

import br.com.manoelegidio.tp4.notification.domain.exception.ResourceNotFoundException;
import br.com.manoelegidio.tp4.notification.domain.model.Notification;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp4.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp4.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationResponseDTO;
import br.com.manoelegidio.tp4.notification.dto.NotificationSummaryDTO;
import br.com.manoelegidio.tp4.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar e salvar uma nova notificação com sucesso")
    void shouldCreateNotification() {
        NotificationRequestDTO request = new NotificationRequestDTO(
                1L, "Tarefa Teste", "manoel@infnet.edu.br",
                "Mensagem de teste", NotificationType.TASK_CREATED, NotificationChannel.IN_APP
        );

        NotificationResponseDTO response = notificationService.createNotification(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Tarefa Teste", response.getTaskTitle());
        assertEquals("manoel@infnet.edu.br", response.getRecipient());
        assertFalse(response.isRead());
    }

    @Test
    @DisplayName("Deve marcar notificação como lida com sucesso")
    void shouldMarkAsRead() {
        NotificationRequestDTO request = new NotificationRequestDTO(
                1L, "Tarefa Teste", "manoel@infnet.edu.br",
                "Mensagem de teste", NotificationType.TASK_CREATED, NotificationChannel.IN_APP
        );
        NotificationResponseDTO created = notificationService.createNotification(request);

        NotificationResponseDTO marked = notificationService.markAsRead(created.getId());

        assertTrue(marked.isRead());
        assertNotNull(marked.getReadAt());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando ID da notificação não existir")
    void shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(9999L));
    }

    @Test
    @DisplayName("Deve calcular resumo de notificações corretamente")
    void shouldCalculateSummaryCorrectly() {
        NotificationRequestDTO r1 = new NotificationRequestDTO(1L, "T1", "manoel@infnet.edu.br", "M1", NotificationType.TASK_CREATED, NotificationChannel.IN_APP);
        NotificationRequestDTO r2 = new NotificationRequestDTO(2L, "T2", "manoel@infnet.edu.br", "M2", NotificationType.STATUS_CHANGED, NotificationChannel.IN_APP);

        NotificationResponseDTO c1 = notificationService.createNotification(r1);
        notificationService.createNotification(r2);

        notificationService.markAsRead(c1.getId());

        NotificationSummaryDTO summary = notificationService.getSummary("manoel@infnet.edu.br");

        assertEquals(2L, summary.getTotalCount());
        assertEquals(1L, summary.getUnreadCount());
        assertEquals(1L, summary.getReadCount());
    }
}
