# Projeto de Bloco - Arquitetura de Software e Microsserviços
**Aluno:** Manoel Egidio • **Engenharia de Softwares Escaláveis (Instituto Infnet)** • **Ano:** 2026

Este repositório consolida a evolução arquitetural completa do sistema **TaskManager PB**, desenvolvido de forma incremental ao longo do semestre letivo através de 5 Trabalhos Práticos (TPs).

Cada etapa possui sua própria pasta independente com código-fonte, suíte de testes automatizados e documentação técnica.

---

## 📁 Estrutura do Repositório

```text
infnet-pb/
├── docs/                       # Documentação Consolidada do Projeto de Bloco
│   └── RELATORIO_CONSOLIDADO_PB.html # Relatório Mestre cobrindo os 27 critérios de rubrica
├── .github/workflows/          # Pipeline de Automação CI/CD (GitHub Actions)
│   └── ci-cd-tp5.yml           # CI/CD: Matriz de Testes, Build Docker e Validação K8s
├── TP1/                        # Etapa 1: Monólito com Spring Boot + React
│   ├── backend/                # Spring Boot 3 em Camadas (Controller, Service, Repository, DDD, H2)
│   ├── frontend/               # Interface React + Vite (Kanban, Categorias e Métricas)
│   └── docs/                   # Documentação de Arquitetura e Relatório TP1
├── TP2/                        # Etapa 2: Camada de Persistência com Spring Data JPA
│   ├── backend/                # Spring Boot + JPA (Mapeamento @ManyToOne, Histórico & Auditoria, JPQL)
│   ├── frontend/               # Interface React com Visualizador de Histórico de Auditoria
│   └── docs/                   # Relatório Técnico TP2 (HTML)
├── TP3/                        # Etapa 3: Microsserviço com Spring Cloud OpenFeign
│   ├── task-service/           # Serviço Central de Tarefas (:8080) com OpenFeign Client & Fallback
│   ├── notification-service/   # Microsserviço Autônomo de Notificações (:8082) com H2 notificationdb
│   ├── frontend/               # Interface React 19 com Central de Notificações e Monitor de Conexão
│   └── docs/                   # Relatório Técnico TP3 (HTML)
├── TP4/                        # Etapa 4: Arquitetura Orientada a Eventos (EDA) com RabbitMQ
│   ├── docker-compose.yml      # Broker RabbitMQ 3.13 com interface de gestão (5672/15672)
│   ├── task-service/           # Produtor de Eventos (Spring AMQP: Direct, Topic, Fanout, DLX) (:8080)
│   ├── notification-service/   # Consumidor de Eventos (@RabbitListener, Retries, Dead Letter Queue) (:8082)
│   ├── frontend/               # Interface React com Simulador EDA de Alta Vazão, DLQ e Fanout (:5173)
│   └── docs/                   # Relatório Técnico TP4 (HTML)
└── TP5/                        # Etapa 5: Implantação e Manutenção em Produção
    ├── docker-compose.prod.yml # Orquestração completa local (Apps, RabbitMQ, Zipkin, Prometheus)
    ├── prometheus.yml          # Configuração de scraping de métricas Actuator
    ├── k8s/                    # 10 Manifestos Kubernetes de Produção
    │   ├── 00-namespace.yaml   # Namespace taskmanager-prod
    │   ├── 01-configmap.yaml   # ConfigMap centralizado
    │   ├── 02-secrets.yaml     # Secrets com credenciais codificadas
    │   ├── 10-rabbitmq.yaml    # Message Broker Deployment & Service
    │   ├── 11-zipkin.yaml      # Distributed Tracing Deployment & Service
    │   ├── 12-prometheus.yaml  # Metrics Server ConfigMap, Deployment & Service
    │   ├── 20-task-service.yaml# Task Service (2 réplicas, RollingUpdate, Probes, HPA 2-10 pods)
    │   ├── 21-notification-service.yaml # Notification Service (Probes, HPA 2-10 pods)
    │   ├── 30-frontend.yaml    # Nginx SPA Deployment & NodePort Service (30080)
    │   └── 40-ingress.yaml     # Regras de Ingress unificadas (taskmanager.local)
    ├── task-service/           # Microsserviço com Actuator Probes, Prometheus e Micrometer Tracing
    ├── notification-service/   # Microsserviço Consumidor com Actuator Probes e Tracing
    ├── frontend/               # Frontend React com Painel de Observabilidade integrado
    └── docs/                   # Relatório Oficial TP5 (HTML)
```

---

## ⚙️ Pré-requisitos Globais

* **Java JDK 21** instalado e configurado na variável de ambiente `JAVA_HOME`
* **Node.js (v20+)** ou **Bun** instalado para os frontends React
* **Docker & Docker Compose** instalado (obrigatório para o broker AMQP no TP4 e stack completa no TP5)
* **Kubectl** (opcional, para homologação dos manifestos de cluster no TP5)

---

## 🛠️ Guia de Execução Passo a Passo (TP1 ao TP5)

### 📌 TP1: Monólito Simples com Spring Boot + React

**1. Iniciar o Backend (Porta 8080):**
```bash
cd TP1/backend
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **API REST:** `http://localhost:8080/api/tasks` e `http://localhost:8080/api/categories`
* **H2 Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskdb`, user: `sa`, password: vazio)
* **Executar Testes:** `./gradlew test` (ou `.\gradlew.bat test`)

**2. Iniciar o Frontend (Porta 5173):**
```bash
cd TP1/frontend
bun install    # ou: npm install
bun run dev    # ou: npm run dev
```

---

### 📌 TP2: Persistência com Spring Data JPA e Auditoria

**1. Iniciar o Backend com Histórico e Auditoria (Porta 8080):**
```bash
cd TP2/backend
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **Endpoints de Tarefas e Categorias:** `http://localhost:8080/api/tasks`
* **Endpoint de Histórico de Auditoria:** `GET http://localhost:8080/api/tasks/{id}/history`
* **Métricas do Domínio:** `GET http://localhost:8080/api/tasks/metrics`
* **Executar Testes:** `./gradlew test` (ou `.\gradlew.bat test`)

**2. Iniciar o Frontend com Visualizador de Histórico (Porta 5173):**
```bash
cd TP2/frontend
bun install    # ou: npm install
bun run dev    # ou: npm run dev
```

---

### 📌 TP3: Microsserviço com Spring Cloud OpenFeign

**1. Iniciar o Microsserviço de Notificações (Porta 8082):**
```bash
cd TP3/notification-service
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **Endpoints de Notificações:** `http://localhost:8082/api/notifications`
* **H2 Console Notificações:** `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:notificationdb`)
* **Executar Testes:** `./gradlew test` (ou `.\gradlew.bat test`)

**2. Iniciar o Serviço Central de Tarefas (Porta 8080):**
```bash
cd TP3/task-service
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **Comunicação Feign:** Integração declarativa via `NotificationClient` com fallback resiliente (`NotificationClientFallback`).
* **Executar Testes:** `./gradlew test` (ou `.\gradlew.bat test`)

**3. Iniciar o Frontend com Central de Notificações:**
```bash
cd TP3/frontend
bun install    # ou: npm install
bun run dev    # ou: npm run dev
```

---

### 📌 TP4: Arquitetura Orientada a Eventos (EDA) com RabbitMQ

**1. Iniciar o Message Broker RabbitMQ (Docker):**
```bash
cd TP4
docker compose up -d
```
* **Protocolo AMQP:** `amqp://localhost:5672` (guest / guest)
* **RabbitMQ Management UI:** `http://localhost:15672`

**2. Iniciar o Consumidor de Eventos (notification-service :8082):**
```bash
cd TP4/notification-service
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **Filas AMQP Ativas:** `@RabbitListener` em `task.notification.queue`, `task.urgent.queue`, `task.broadcast.queue` e `task.dead-letter.queue`.
* **Endpoint da DLQ:** `http://localhost:8082/api/notifications/dlq`
* **Executar Testes AMQP:** `./gradlew test` (ou `.\gradlew.bat test`)

**3. Iniciar o Produtor de Eventos (task-service :8080):**
```bash
cd TP4/task-service
# Linux / macOS:
./gradlew bootRun
# Windows:
.\gradlew.bat bootRun
```
* **Endpoints de Simulação de Carga e Falhas:**
  * Rajada de Eventos: `POST /api/tasks/simulation/burst?count=20`
  * Mensagem Venenosa (DLQ): `POST /api/tasks/simulation/poison-pill`
  * Broadcast Fanout: `POST /api/tasks/simulation/broadcast`
* **Executar Testes AMQP:** `./gradlew test` (ou `.\gradlew.bat test`)

**4. Iniciar o Frontend com Simulador EDA:**
```bash
cd TP4/frontend
bun install    # ou: npm install
bun run dev    # ou: npm run dev
```

---

### 📌 TP5: Implantação e Manutenção em Produção (Docker, K8s & Observabilidade)

#### Modo de Execução 1: Docker Compose de Produção
```bash
cd TP5
docker compose -f docker-compose.prod.yml up -d --build
```
* **Frontend Web (Nginx SPA):** `http://localhost:3000`
* **Task Service Actuator:** `http://localhost:8080/actuator/health`
* **Notification Service Actuator:** `http://localhost:8082/actuator/health`
* **Métricas Prometheus:** `http://localhost:8080/actuator/prometheus` e `http://localhost:9090`
* **Rastreamento Distribuído Zipkin:** `http://localhost:9411`
* **RabbitMQ Management:** `http://localhost:15672` (guest / guest)

#### Modo de Execução 2: Implantação no Cluster Kubernetes (K8s)
```bash
# Aplicar todos os manifestos de produção
kubectl apply -f TP5/k8s/00-namespace.yaml
kubectl apply -f TP5/k8s/01-configmap.yaml
kubectl apply -f TP5/k8s/02-secrets.yaml
kubectl apply -f TP5/k8s/10-rabbitmq.yaml
kubectl apply -f TP5/k8s/11-zipkin.yaml
kubectl apply -f TP5/k8s/12-prometheus.yaml
kubectl apply -f TP5/k8s/20-task-service.yaml
kubectl apply -f TP5/k8s/21-notification-service.yaml
kubectl apply -f TP5/k8s/30-frontend.yaml
kubectl apply -f TP5/k8s/40-ingress.yaml

# Verificar pods, serviços e HPAs (auto-escalonamento de 2 a 10 réplicas)
kubectl get all,hpa,ingress -n taskmanager-prod
```

#### Modo de Execução 3: Desenvolvimento Local com Probes Ativas
```bash
# 1. Executar suítes de testes com validação de Actuator e Probes K8s
# Linux / macOS:
cd TP5/task-service && ./gradlew test
cd ../notification-service && ./gradlew test

# Windows:
cd TP5/task-service; .\gradlew.bat test
cd ../notification-service; .\gradlew.bat test

# 2. Iniciar o frontend de produção com Painel de Observabilidade
cd ../frontend
bun install    # ou: npm install
bun run dev    # ou: npm run dev
```

---

## 🧪 Resumo dos Testes Automatizados (100% de Aprovação)

Todas as suítes de testes automatizados do projeto foram executadas e validadas:

| Etapa | Módulo | Foco de Testes | Ferramental | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TP1** | `TP1/backend` | Camadas, DTOs e Regras de Negócio | JUnit 5, Spring Boot Test | **Aprovado** |
| **TP2** | `TP2/backend` | Mapeamento ORM, Queries JPQL e Auditoria | JUnit 5, Spring Data JPA | **Aprovado** |
| **TP3** | `TP3/task-service` | Feign Client, Resiliência e Fallback | Spring Cloud Contract / JUnit | **Aprovado** |
| **TP3** | `TP3/notification-service` | Controladores, Serviços e Repositórios | MockMvc, Spring Boot Test | **Aprovado** |
| **TP4** | `TP4/task-service` | Produtor AMQP (Direct, Topic, Fanout) | Spring Rabbit Test, Mockito | **Aprovado** |
| **TP4** | `TP4/notification-service` | Consumidor AMQP, Retries e DLQ | Spring Rabbit Test, Mockito | **Aprovado** |
| **TP5** | `TP5/task-service` | Probes K8s (Liveness/Readiness) e Prometheus | Spring Boot Actuator, MockMvc | **Aprovado** |
| **TP5** | `TP5/notification-service` | Probes K8s e Micrometer Tracing | Spring Boot Actuator, MockMvc | **Aprovado** |

---

## 📋 Atendimento Geral das Rubricas do Projeto de Bloco

A documentação detalhada cobrindo todos os **27 critérios da rubrica de avaliação** do semestre (do monólito inicial à operação em nuvem) encontra-se em:
* **Relatório Técnico Consolidado:** [`docs/RELATORIO_CONSOLIDADO_PB.html`](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/docs/RELATORIO_CONSOLIDADO_PB.html)
* **Relatório da Etapa 3:** [`TP3/docs/RELATORIO_TP3.html`](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP3/docs/RELATORIO_TP3.html)
* **Relatório da Etapa 4:** [`TP4/docs/RELATORIO_TP4.html`](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP4/docs/RELATORIO_TP4.html)
* **Relatório da Etapa 5:** [`TP5/docs/RELATORIO_TP5.html`](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP5/docs/RELATORIO_TP5.html)

*(Os arquivos PDF oficiais foram gerados localmente conforme as regras nominais institucionais para upload no portal da disciplina).*