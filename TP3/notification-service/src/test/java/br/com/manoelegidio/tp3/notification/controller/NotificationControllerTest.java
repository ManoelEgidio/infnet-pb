package br.com.manoelegidio.tp3.notification.controller;

import br.com.manoelegidio.tp3.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp3.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp3.notification.dto.NotificationRequestDTO;
import br.com.manoelegidio.tp3.notification.dto.NotificationResponseDTO;
import br.com.manoelegidio.tp3.notification.dto.NotificationSummaryDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationControllerTest {

    @Autowired
    private NotificationController notificationController;

    @Test
    @DisplayName("POST /api/notifications - Deve criar notificação e retornar 201 Created")
    void shouldCreateNotification() {
        NotificationRequestDTO request = new NotificationRequestDTO(
                1L, "Deploy em Produção", "manoel@infnet.edu.br",
                "Deploy agendado para 22h", NotificationType.HIGH_PRIORITY_ALERT, NotificationChannel.IN_APP
        );

        ResponseEntity<NotificationResponseDTO> response = notificationController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Deploy em Produção", response.getBody().getTaskTitle());
        assertEquals("manoel@infnet.edu.br", response.getBody().getRecipient());
        assertEquals(NotificationType.HIGH_PRIORITY_ALERT, response.getBody().getType());
    }

    @Test
    @DisplayName("GET /api/notifications - Deve listar notificações e retornar 200 OK")
    void shouldListNotifications() {
        NotificationRequestDTO req = new NotificationRequestDTO(
                2L, "Tarefa Controller", "manoel@infnet.edu.br",
                "Msg Controller", NotificationType.TASK_CREATED, NotificationChannel.IN_APP
        );
        notificationController.create(req);

        ResponseEntity<List<NotificationResponseDTO>> response = notificationController.getAll(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("GET /api/notifications/summary - Deve retornar resumo de contadores")
    void shouldGetSummary() {
        ResponseEntity<NotificationSummaryDTO> response = notificationController.getSummary(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalCount() >= 0);
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read - Deve marcar notificação como lida")
    void shouldMarkAsRead() {
        NotificationRequestDTO req = new NotificationRequestDTO(
                3L, "Tarefa Leitura", "manoel@infnet.edu.br",
                "Msg Leitura", NotificationType.TASK_CREATED, NotificationChannel.IN_APP
        );
        ResponseEntity<NotificationResponseDTO> created = notificationController.create(req);
        Long id = created.getBody().getId();

        ResponseEntity<NotificationResponseDTO> response = notificationController.markAsRead(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isRead());
    }

    @Test
    @DisplayName("PATCH /api/notifications/read-all - Deve marcar todas como lidas")
    void shouldMarkAllAsRead() {
        ResponseEntity<Map<String, Object>> response = notificationController.markAllAsRead(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("updatedCount"));
    }
}
