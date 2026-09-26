const API_BASE = 'http://localhost:8080/api';

export async function fetchTasks(status = '', priority = '', categoryId = '') {
  let url = `${API_BASE}/tasks`;
  const params = new URLSearchParams();
  if (status) params.append('status', status);
  if (priority) params.append('priority', priority);
  if (categoryId) params.append('categoryId', categoryId);
  
  if (params.toString()) {
    url += `?${params.toString()}`;
  }

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('Falha ao carregar as tarefas.');
  }
  return response.json();
}

export async function fetchMetrics() {
  const response = await fetch(`${API_BASE}/tasks/metrics`);
  if (!response.ok) {
    throw new Error('Falha ao carregar as métricas do painel.');
  }
  return response.json();
}

export async function fetchCategories() {
  const response = await fetch(`${API_BASE}/categories`);
  if (!response.ok) {
    throw new Error('Falha ao carregar categorias.');
  }
  return response.json();
}

export async function fetchTaskHistory(taskId) {
  const response = await fetch(`${API_BASE}/tasks/${taskId}/history`);
  if (!response.ok) {
    throw new Error('Falha ao carregar o histórico da tarefa.');
  }
  return response.json();
}

export async function createTask(taskData) {
  const response = await fetch(`${API_BASE}/tasks`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskData),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Falha ao criar tarefa.');
  }
  return response.json();
}

export async function updateTask(id, taskData) {
  const response = await fetch(`${API_BASE}/tasks/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(taskData),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Falha ao atualizar tarefa.');
  }
  return response.json();
}

export async function updateTaskStatus(id, status, updatedBy = 'Usuário Web') {
  const response = await fetch(`${API_BASE}/tasks/${id}/status?status=${status}&updatedBy=${encodeURIComponent(updatedBy)}`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Falha ao atualizar o status da tarefa.');
  }
  return response.json();
}

export async function deleteTask(id, deletedBy = 'Usuário Web') {
  const response = await fetch(`${API_BASE}/tasks/${id}?deletedBy=${encodeURIComponent(deletedBy)}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('Falha ao excluir tarefa.');
  }
  return true;
}

// =========================================================================
// Simulação e Topologia RabbitMQ (Rubrica TP4)
// =========================================================================

export async function simulateBurst(count = 15) {
  const response = await fetch(`${API_BASE}/tasks/simulation/burst?count=${count}`, {
    method: 'POST'
  });
  if (!response.ok) throw new Error('Falha ao simular rajada de mensagens.');
  return response.json();
}

export async function simulatePoisonPill(taskId = 99999) {
  const response = await fetch(`${API_BASE}/tasks/simulation/poison-pill?taskId=${taskId}`, {
    method: 'POST'
  });
  if (!response.ok) throw new Error('Falha ao simular mensagem venenosa para DLQ.');
  return response.json();
}

export async function simulateBroadcast(message, actor) {
  const response = await fetch(`${API_BASE}/tasks/simulation/broadcast`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, actor })
  });
  if (!response.ok) throw new Error('Falha ao disparar broadcast via Fanout.');
  return response.json();
}

export async function fetchTopology() {
  const response = await fetch(`${API_BASE}/tasks/simulation/topology`);
  if (!response.ok) throw new Error('Falha ao consultar topologia do RabbitMQ.');
  return response.json();
}
