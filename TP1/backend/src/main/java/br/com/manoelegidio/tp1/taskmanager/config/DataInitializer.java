package br.com.manoelegidio.tp1.taskmanager.config;

import br.com.manoelegidio.tp1.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp1.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp1.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp1.taskmanager.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskService taskService;

    public DataInitializer(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (taskService.getAllTasks(null, null).isEmpty()) {
            taskService.createTask(new TaskRequestDTO(
                    "Modelagem de Domínio DDD",
                    "Definir Value Objects, Entidades e Bounded Contexts para a entrega TP1.",
                    Priority.HIGH,
                    TaskStatus.COMPLETED,
                    LocalDateTime.now().plusDays(2)
            ));

            taskService.createTask(new TaskRequestDTO(
                    "Construção da API Spring Boot",
                    "Implementar Controller, Service e Repository aplicando princípios SOLID.",
                    Priority.URGENT,
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.now().plusDays(5)
            ));

            taskService.createTask(new TaskRequestDTO(
                    "Desenvolvimento da Interface React",
                    "Criar Dashboard e formulários interativos consumindo os endpoints REST.",
                    Priority.HIGH,
                    TaskStatus.PENDING,
                    LocalDateTime.now().plusDays(7)
            ));

            taskService.createTask(new TaskRequestDTO(
                    "Documentação Arquitetural e Diagramas",
                    "Elaborar os diagramas de componentes e sequência em Mermaid para a entrega em PDF.",
                    Priority.MEDIUM,
                    TaskStatus.PENDING,
                    LocalDateTime.now().plusDays(10)
            ));
        }
    }
}
