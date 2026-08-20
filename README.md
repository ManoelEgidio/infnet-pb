# Projeto de Bloco - Arquitetura de Software e Microsserviços

Este repositório está organizado em pastas separadas para cada Trabalho Prático (TP) da disciplina. Cada pasta contém seu próprio código fonte e documentação, permitindo a execução independente ou o envio separado para o GitHub.

---

## 📁 Estrutura de Pastas

```text
projeto de bloco/
├── TP1/                    # Primeira Entrega: Monólito Simples com Spring Boot + React
│   ├── backend/            # Aplicação Spring Boot 3 (API REST, DDD, JPA, H2)
│   ├── frontend/           # Interface React + Vite (Dashboard, Kanban, Vanilla CSS)
│   └── docs/               # Documentação Arquitetural e Relatório TP1
├── TP2/                    # Segunda Entrega: Camada de Persistência Real com Spring Data JPA
│   ├── backend/            # Spring Boot + JPA (Relacionamentos @ManyToOne, Histórico & Auditoria, Queries JPQL)
│   ├── frontend/           # Interface React com Visualizador e Timeline de Auditoria de Dados
│   └── docs/               # Relatório Técnico TP2 (Manoel_Egidio_PB_TP2.html / PDF)
├── TP3/                    # Terceira Entrega (Reservado)
└── TP4/                    # Quarta Entrega (Reservado)
```

---

## 🚀 Como Executar o TP2 (Segunda Entrega)

### Backend (Spring Boot + JPA + Auditoria)
```bash
cd TP2/backend
./gradlew bootRun
```
* **Endpoints da API:** `http://localhost:8080/api/tasks` e `http://localhost:8080/api/categories`
* **Histórico de Auditoria:** `http://localhost:8080/api/tasks/{id}/history`
* **H2 Console:** `http://localhost:8080/h2-console`
* **Testes Automatizados:** `./gradlew test`

### Frontend (React + Vite)
```bash
cd TP2/frontend
npm install
npm run dev
```
Acesse em: `http://localhost:5173`