const API_BASE_URL = 'http://localhost:8080/api/tasks';

export async function fetchTasks(status = '', priority = '') {
  let url = API_BASE_URL;
  const params = new URLSearchParams();
  if (status) params.append('status', status);
  if (priority) params.append('priority', priority);
  
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
  const response = await fetch(`${API_BASE_URL}/metrics`);
  if (!response.ok) {
    throw new Error('Falha ao carregar as métricas do painel.');
  }
  return response.json();
}

export async function createTask(taskData) {
  const response = await fetch(API_BASE_URL, {
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
  const response = await fetch(`${API_BASE_URL}/${id}`, {
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

export async function updateTaskStatus(id, status) {
  const response = await fetch(`${API_BASE_URL}/${id}/status?status=${status}`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Falha ao atualizar o status da tarefa.');
  }
  return response.json();
}

export async function deleteTask(id) {
  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('Falha ao excluir tarefa.');
  }
  return true;
}
