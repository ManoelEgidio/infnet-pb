package br.com.manoelegidio.tp4.notification.repository;

import br.com.manoelegidio.tp4.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);

    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(String recipient);

    List<Notification> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    long countByIsReadFalse();

    long countByRecipientAndIsReadFalse(String recipient);

    long countByRecipient(String recipient);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipient = :recipient AND n.isRead = false")
    int markAllAsReadByRecipient(@Param("recipient") String recipient);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.isRead = false")
    int markAllAsRead();
}
