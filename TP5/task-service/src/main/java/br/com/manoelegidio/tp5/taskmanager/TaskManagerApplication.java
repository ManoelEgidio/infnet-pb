package br.com.manoelegidio.tp5.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TaskManager Application - TP5: Arquitetura Orientada a Eventos (EDA).
 * Desacoplado de chamadas síncronas HTTP/Feign e integrado com RabbitMQ Message Broker.
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
