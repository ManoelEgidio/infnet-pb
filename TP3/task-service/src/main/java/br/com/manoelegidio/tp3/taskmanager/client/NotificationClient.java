package br.com.manoelegidio.tp3.taskmanager.client;

import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationPayloadDTO;
import br.com.manoelegidio.tp3.taskmanager.client.dto.NotificationResponseClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "notification-service",
        url = "${notification.service.url:http://localhost:8082}",
        fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/api/notifications")
    NotificationResponseClientDTO sendNotification(@RequestBody NotificationPayloadDTO request);

    @GetMapping("/api/notifications/task/{taskId}")
    List<NotificationResponseClientDTO> getNotificationsByTaskId(@PathVariable("taskId") Long taskId);
}
