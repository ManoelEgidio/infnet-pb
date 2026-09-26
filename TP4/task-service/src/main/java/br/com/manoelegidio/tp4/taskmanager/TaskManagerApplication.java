package br.com.manoelegidio.tp4.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TaskManager Application - TP4: Arquitetura Orientada a Eventos (EDA).
 * Desacoplado de chamadas síncronas HTTP/Feign e integrado com RabbitMQ Message Broker.
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
