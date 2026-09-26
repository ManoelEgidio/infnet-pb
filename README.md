# Projeto de Bloco - Arquitetura de Software e Microsserviços
**Aluno:** Manoel Egidio • **Engenharia de Softwares Escaláveis (Instituto Infnet)**

Este repositório está organizado em pastas modulares para cada Trabalho Prático (TP) da disciplina. Cada pasta contém seu próprio código fonte, testes e documentação arquitetural.

---

## 📁 Estrutura de Pastas

```text
infnet-pb/
├── .github/workflows/          # Pipeline de Automação CI/CD (GitHub Actions)
│   └── ci-cd-tp5.yml           # CI/CD: Matriz de Testes, Build Docker e Validação K8s
├── TP1/                        # Primeira Entrega: Monólito com Spring Boot + React
│   ├── backend/                # Aplicação Spring Boot 3 (API REST, DDD, JPA, H2)
│   ├── frontend/               # Interface React + Vite (Dashboard, Kanban, Vanilla CSS)
│   └── docs/                   # Documentação Arquitetural e Relatório TP1
├── TP2/                        # Segunda Entrega: Camada de Persistência com Spring Data JPA
│   ├── backend/                # Spring Boot + JPA (Relacionamentos @ManyToOne, Histórico & Auditoria, JPQL)
│   ├── frontend/               # Interface React com Visualizador de Histórico e Auditoria
│   └── docs/                   # Relatório Técnico TP2 (Manoel_Egidio_PB_TP2.pdf)
├── TP3/                        # Terceira Entrega: Microsserviço com Spring Cloud OpenFeign
│   ├── task-service/           # Serviço Central de Tarefas (:8080) com OpenFeign Client & Fallback
│   ├── notification-service/   # Microsserviço Autônomo de Notificações (:8082) com H2 notificationdb
│   ├── frontend/               # Interface React 19 + Vite com Custom Selects, Drawer e Conectividade
│   └── docs/                   # Relatório Técnico TP3 (Manoel_Egidio_PB_TP3.pdf & HTML)
├── TP4/                        # Quarta Entrega: Arquitetura Orientada a Eventos (EDA) com RabbitMQ
│   ├── docker-compose.yml      # Broker RabbitMQ 3.13 com interface de gerenciamento (5672/15672)
│   ├── task-service/           # Produtor de Eventos (Spring AMQP, Direct, Topic, Fanout, DLX) (:8080)
│   ├── notification-service/   # Consumidor de Eventos (@RabbitListener, Retries, Dead Letter Queue) (:8082)
│   ├── frontend/               # Interface React com Simulador EDA de Alta Vazão, DLQ e Fanout (:5173)
│   └── docs/                   # Relatório Técnico TP4 (Manoel_Egidio_PB_TP4.pdf & HTML)
└── TP5/                        # Última Entrega: Implantação e Manutenção em Produção
    ├── docker-compose.prod.yml # Orquestração completa local (Apps, RabbitMQ, Zipkin, Prometheus)
    ├── prometheus.yml          # Configuração de scraping de métricas Actuator
    ├── k8s/                    # Manifestos Kubernetes de Produção
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
    └── docs/                   # Relatório Oficial TP5 (Manoel_Egidio_PB_TP5.pdf & HTML)
```

---

## 🚀 Como Executar o TP5 (Última Entrega - Implantação em Produção)

### Opção 1: Execução com Docker Compose de Produção
```bash
cd TP5
docker compose -f docker-compose.prod.yml up -d --build
```
* **Frontend Web (Nginx SPA):** `http://localhost:3000`
* **Task Service API & Probes:** `http://localhost:8080/actuator/health`
* **Notification Service API & Probes:** `http://localhost:8082/actuator/health`
* **Métricas Abertas Prometheus:** `http://localhost:8080/actuator/prometheus` e `http://localhost:9090`
* **Rastreamento Distribuído Zipkin:** `http://localhost:9411`
* **Painel RabbitMQ:** `http://localhost:15672` (guest / guest)

### Opção 2: Implantação no Cluster Kubernetes (K8s)
```bash
# Aplicar toda a topologia no cluster
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

# Inspecionar recursos e auto-escalonamento horizontal
kubectl get all -n taskmanager-prod
kubectl get hpa -n taskmanager-prod
```

### Opção 3: Execução e Testes em Ambiente de Desenvolvimento
```bash
# 1. Executar testes automatizados do backend (100% aprovados)
cd TP5/task-service && ./gradlew test
cd TP5/notification-service && ./gradlew test

# 2. Executar frontend em modo dev
cd TP5/frontend
bun install
bun run dev
```

---

## 📄 Relatórios Técnicos Oficiais (PDF)

* **TP1:** [Relatório Técnico TP1](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP1/docs/RELATORIO_TP1.md)
* **TP2:** [Manoel_Egidio_PB_TP2.pdf](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP2/docs/Manoel_Egidio_PB_TP2.pdf)
* **TP3:** [Manoel_Egidio_PB_TP3.pdf](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP3/docs/Manoel_Egidio_PB_TP3.pdf)
* **TP4:** [Manoel_Egidio_PB_TP4.pdf](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP4/docs/Manoel_Egidio_PB_TP4.pdf)
* **TP5 (Final):** [Manoel_Egidio_PB_TP5.pdf](file:///c:/Users/Manoel/Documents/GitHub/infnet-pb/TP5/docs/Manoel_Egidio_PB_TP5.pdf) (246 KB, gerado conforme regra nominal `nome_sobrenome_PB_TP5.PDF`)