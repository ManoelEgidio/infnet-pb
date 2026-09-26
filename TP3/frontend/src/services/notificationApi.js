const NOTIFICATION_API_BASE = 'http://localhost:8082/api/notifications';

export async function fetchNotifications(recipient = '', unreadOnly = false) {
  let url = `${NOTIFICATION_API_BASE}`;
  const params = new URLSearchParams();
  if (recipient) params.append('recipient', recipient);
  if (unreadOnly) params.append('unreadOnly', 'true');

  if (params.toString()) {
    url += `?${params.toString()}`;
  }

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('Falha ao obter notificações do microsserviço.');
  }
  return response.json();
}

export async function fetchNotificationSummary(recipient = '') {
  let url = `${NOTIFICATION_API_BASE}/summary`;
  if (recipient) {
    url += `?recipient=${encodeURIComponent(recipient)}`;
  }

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('Falha ao obter resumo de notificações.');
  }
  return response.json();
}

export async function markNotificationAsRead(id) {
  const response = await fetch(`${NOTIFICATION_API_BASE}/${id}/read`, {
    method: 'PATCH',
  });
  if (!response.ok) {
    throw new Error('Falha ao marcar notificação como lida.');
  }
  return response.json();
}

export async function markAllNotificationsAsRead(recipient = '') {
  let url = `${NOTIFICATION_API_BASE}/read-all`;
  if (recipient) {
    url += `?recipient=${encodeURIComponent(recipient)}`;
  }
  const response = await fetch(url, {
    method: 'PATCH',
  });
  if (!response.ok) {
    throw new Error('Falha ao marcar todas as notificações como lidas.');
  }
  return response.json();
}

export async function sendBroadcastAlert(message) {
  const response = await fetch(`${NOTIFICATION_API_BASE}/broadcast?message=${encodeURIComponent(message)}`, {
    method: 'POST',
  });
  if (!response.ok) {
    throw new Error('Falha ao emitir alerta broadcast.');
  }
  return response.json();
}

export async function checkServicesHealth() {
  const results = {
    taskService: false,
    notificationService: false,
  };

  try {
    const res1 = await fetch('http://localhost:8080/api/tasks/metrics', { signal: AbortSignal.timeout(2000) });
    results.taskService = res1.ok;
  } catch {
    results.taskService = false;
  }

  try {
    const res2 = await fetch('http://localhost:8082/api/notifications/summary', { signal: AbortSignal.timeout(2000) });
    results.notificationService = res2.ok;
  } catch {
    results.notificationService = false;
  }

  return results;
}
