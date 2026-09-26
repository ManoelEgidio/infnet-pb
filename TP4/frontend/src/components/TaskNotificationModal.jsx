import React, { useState, useEffect } from 'react';
import { X, Bell, RefreshCw, AlertTriangle, CheckCircle2, Info, ArrowRight } from 'lucide-react';
import { fetchTaskNotifications } from '../services/api';

export default function TaskNotificationModal({ task, isOpen, onClose }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen && task) {
      loadTaskNotifications();
    }
  }, [isOpen, task]);

  const loadTaskNotifications = async () => {
    if (!task) return;
    setLoading(true);
    try {
      const data = await fetchTaskNotifications(task.id);
      setNotifications(data || []);
    } catch (err) {
      console.error('Erro ao carregar notificações distribuídas da tarefa:', err);
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !task) return null;

  const getTypeIcon = (type) => {
    switch (type) {
      case 'HIGH_PRIORITY_ALERT':
        return <AlertTriangle size={18} className="text-warning" />;
      case 'TASK_COMPLETED':
        return <CheckCircle2 size={18} className="text-success" />;
      case 'STATUS_CHANGED':
        return <RefreshCw size={18} className="text-info" />;
      default:
        return <Info size={18} className="text-primary" />;
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content modal-distributed-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-title-wrap">
            <div className="modal-header-icon-box alert">
              <Bell size={22} />
            </div>
            <div>
              <h2 className="modal-title">Notificações Distribuídas (Spring Cloud)</h2>
              <div className="distributed-route-badge">
                <span>task-service (:8080)</span>
                <ArrowRight size={12} />
                <span>FeignClient</span>
                <ArrowRight size={12} />
                <span>notification-service (:8082)</span>
              </div>
            </div>
          </div>
          <button className="btn-icon-only modal-close-btn" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        {/* Task Summary Banner */}
        <div className="task-summary-banner">
          <div className="task-summary-title">Tarefa #{task.id}: {task.title}</div>
          <div className="task-summary-desc">{task.description || 'Sem descrição cadastrada'}</div>
        </div>

        <div className="modal-scroll-body">
          {loading ? (
            <div className="modal-empty-state">
              <div className="spinner-glow"></div>
              <p>Consultando microsserviço de notificações via Feign Client...</p>
            </div>
          ) : notifications.length === 0 ? (
            <div className="modal-empty-state">
              <Bell size={36} className="modal-empty-icon" />
              <h4>Nenhuma notificação registrada</h4>
              <p>O microsserviço ainda não registrou eventos assíncronos para esta tarefa.</p>
            </div>
          ) : (
            <div className="distributed-notifications-list">
              {notifications.map((n, idx) => (
                <div
                  key={n.id || idx}
                  className={`distributed-item-card ${n.isRead ? 'read' : 'unread'} ${
                    n.type === 'HIGH_PRIORITY_ALERT' ? 'high-alert' : ''
                  }`}
                >
                  <div className="distributed-icon-wrapper">{getTypeIcon(n.type)}</div>
                  <div className="distributed-content-wrapper">
                    <div className="distributed-header-row">
                      <span className="distributed-type-tag">
                        {n.typeDescription || n.type}
                      </span>
                      <span className="distributed-timestamp">
                        {n.createdAt
                          ? new Date(n.createdAt).toLocaleTimeString([], {
                              hour: '2-digit',
                              minute: '2-digit'
                            }) + ' • ' + new Date(n.createdAt).toLocaleDateString('pt-BR')
                          : ''}
                      </span>
                    </div>

                    <p className="distributed-message">{n.message}</p>

                    <div className="distributed-meta-row">
                      <span className="meta-pill">
                        Destinatário: <strong>{n.recipient}</strong>
                      </span>
                      <span className="meta-pill">
                        Canal: <strong>{n.channelLabel || n.channel}</strong>
                      </span>
                      <span className={`status-pill ${n.isRead ? 'read' : 'unread'}`}>
                        {n.isRead ? 'Lida' : 'Não lida'}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="modal-actions" style={{ marginTop: '20px' }}>
          <button className="btn btn-secondary" onClick={onClose}>
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
