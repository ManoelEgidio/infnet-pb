package br.com.manoelegidio.tp5.notification.config;

import br.com.manoelegidio.tp5.notification.domain.model.Notification;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationChannel;
import br.com.manoelegidio.tp5.notification.domain.model.NotificationType;
import br.com.manoelegidio.tp5.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final NotificationRepository notificationRepository;

    public DataInitializer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        if (notificationRepository.count() == 0) {
            log.info("Inicializando dados de exemplo para o Microsserviço de Notificações...");

            Notification n1 = new Notification(
                    1L,
                    "Configurar Pipeline CI/CD",
                    "manoel@infnet.edu.br",
                    "A tarefa #1 'Configurar Pipeline CI/CD' foi criada no quadro Kanban com prioridade ALTA.",
                    NotificationType.HIGH_PRIORITY_ALERT,
                    NotificationChannel.IN_APP
            );
            n1.setCreatedAt(LocalDateTime.now().minusHours(2));

            Notification n2 = new Notification(
                    2L,
                    "Refatorar Camada de Domínio",
                    "manoel@infnet.edu.br",
                    "O status da tarefa #2 mudou de TODO para IN_PROGRESS pelo usuário Manoel.",
                    NotificationType.STATUS_CHANGED,
                    NotificationChannel.IN_APP
            );
            n2.setCreatedAt(LocalDateTime.now().minusHours(1));

            Notification n3 = new Notification(
                    3L,
                    "Desenvolver Microsserviço de Notificações",
                    "manoel@infnet.edu.br",
                    "Parabéns! A tarefa #3 'Desenvolver Microsserviço de Notificações' foi marcada como CONCLUÍDA.",
                    NotificationType.TASK_COMPLETED,
                    NotificationChannel.IN_APP
            );
            n3.setCreatedAt(LocalDateTime.now().minusMinutes(20));

            Notification n4 = new Notification(
                    null,
                    "Infraestrutura de Microsserviços",
                    "manoel@infnet.edu.br",
                    "O microsserviço de notificações foi inicializado com sucesso na porta 8082.",
                    NotificationType.SYSTEM_ALERT,
                    NotificationChannel.IN_APP
            );
            n4.setCreatedAt(LocalDateTime.now().minusMinutes(5));

            notificationRepository.save(n1);
            notificationRepository.save(n2);
            notificationRepository.save(n3);
            notificationRepository.save(n4);

            log.info("Carga inicial de 4 notificações criada no NotificationRepository.");
        }
    }
}
