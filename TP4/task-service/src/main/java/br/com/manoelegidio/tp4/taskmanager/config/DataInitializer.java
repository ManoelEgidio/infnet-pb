package br.com.manoelegidio.tp4.taskmanager.config;

import br.com.manoelegidio.tp4.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp4.taskmanager.domain.model.TaskStatus;
import br.com.manoelegidio.tp4.taskmanager.dto.CategoryDTO;
import br.com.manoelegidio.tp4.taskmanager.dto.TaskRequestDTO;
import br.com.manoelegidio.tp4.taskmanager.service.CategoryService;
import br.com.manoelegidio.tp4.taskmanager.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CategoryService categoryService, TaskService taskService) {
        return args -> {
            // Cria Categorias Iniciais
            CategoryDTO backendCat = categoryService.createCategory("Backend & Persistência", "Módulo de API, JPA e Banco de Dados", "#3b82f6");
            CategoryDTO frontendCat = categoryService.createCategory("Frontend & UI", "Interface React, Tailwind e Componentes", "#10b981");
            CategoryDTO devopsCat = categoryService.createCategory("DevOps & Infra", "CI/CD, Docker e Monitoramento", "#8b5cf6");

            // Cria Tarefas Iniciais com rastreabilidade de histórico
            var task1 = taskService.createTask(new TaskRequestDTO(
                    "Modelar Entidades JPA e Repositórios",
                    "Criar relacionamentos @ManyToOne, índices e métodos de busca customizados.",
                    Priority.URGENT,
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.now().plusDays(2),
                    backendCat.id(),
                    "Manoel Egidio"
            ));

            var task2 = taskService.createTask(new TaskRequestDTO(
                    "Desenvolver Interface de Auditoria de Dados",
                    "Construir timeline interativa para visualização do histórico de cada tarefa.",
                    Priority.HIGH,
                    TaskStatus.PENDING,
                    LocalDateTime.now().plusDays(5),
                    frontendCat.id(),
                    "Manoel Egidio"
            ));

            var task3 = taskService.createTask(new TaskRequestDTO(
                    "Elaborar Testes de Repositório @DataJpaTest",
                    "Cobrir queries JPQL, constraints e regras de integridade com Spring Boot Test.",
                    Priority.MEDIUM,
                    TaskStatus.COMPLETED,
                    LocalDateTime.now().plusDays(1),
                    backendCat.id(),
                    "Manoel Egidio"
            ));

            // Simula uma alteração posterior para gerar histórico rico
            taskService.updateTaskStatus(task1.id(), TaskStatus.IN_PROGRESS, "Sistema / Auditoria");
        };
    }
}
