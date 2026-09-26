import React, { useState, useEffect } from 'react';
import { 
  Bell, Check, CheckCheck, X, RefreshCw, AlertTriangle, 
  CheckCircle2, Info, Send, Radio
} from 'lucide-react';
import { 
  fetchNotifications, markNotificationAsRead, markAllNotificationsAsRead, 
  sendBroadcastAlert 
} from '../services/notificationApi';

export default function NotificationCenter({ isOpen, onClose, onNotificationCountChange }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState('ALL'); // 'ALL' | 'UNREAD' | 'ALERTS'
  const [broadcastMessage, setBroadcastMessage] = useState('');
  const [isSendingBroadcast, setIsSendingBroadcast] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const loadNotifications = async () => {
    setLoading(true);
    try {
      const data = await fetchNotifications();
      setNotifications(data);
      const unread = data.filter(n => !n.isRead).length;
      if (onNotificationCountChange) {
        onNotificationCountChange(unread);
      }
    } catch (err) {
      console.error('Erro ao buscar notificações:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      loadNotifications();
    }
  }, [isOpen]);

  const handleMarkAsRead = async (id, e) => {
    e.stopPropagation();
    try {
      await markNotificationAsRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true, readAt: new Date().toISOString() } : n));
      const newUnread = notifications.filter(n => n.id !== id && !n.isRead).length;
      if (onNotificationCountChange) onNotificationCountChange(newUnread);
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await markAllNotificationsAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true, readAt: new Date().toISOString() })));
      if (onNotificationCountChange) onNotificationCountChange(0);
      showFeedback('Todas as notificações foram marcadas como lidas!');
    } catch (err) {
      console.error(err);
    }
  };

  const handleSendBroadcast = async (e) => {
    e.preventDefault();
    if (!broadcastMessage.trim()) return;

    setIsSendingBroadcast(true);
    try {
      await sendBroadcastAlert(broadcastMessage);
      setBroadcastMessage('');
      showFeedback('Alerta broadcast emitido para todos os usuários!');
      await loadNotifications();
    } catch (err) {
      console.error(err);
      showFeedback('Erro ao emitir alerta broadcast.', true);
    } finally {
      setIsSendingBroadcast(false);
    }
  };

  const showFeedback = (msg, isError = false) => {
    setFeedback({ msg, isError });
    setTimeout(() => setFeedback(null), 3500);
  };

  if (!isOpen) return null;

  const filteredNotifications = notifications.filter(n => {
    if (filter === 'UNREAD') return !n.isRead;
    if (filter === 'ALERTS') return n.type === 'HIGH_PRIORITY_ALERT' || n.type === 'SYSTEM_ALERT';
    return true;
  });

  const unreadCount = notifications.filter(n => !n.isRead).length;

  const getTypeIcon = (type) => {
    switch (type) {
      case 'HIGH_PRIORITY_ALERT':
        return <AlertTriangle size={18} className="text-amber-500" />;
      case 'TASK_COMPLETED':
        return <CheckCircle2 size={18} className="text-emerald-500" />;
      case 'STATUS_CHANGED':
        return <RefreshCw size={18} className="text-blue-500" />;
      case 'TASK_CREATED':
        return <Bell size={18} className="text-indigo-500" />;
      default:
        return <Info size={18} className="text-purple-500" />;
    }
  };

  const formatTimestamp = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' • ' + date.toLocaleDateString();
  };

  return (
    <div className="notification-drawer-overlay" onClick={onClose}>
      <div className="notification-drawer" onClick={e => e.stopPropagation()}>
        {/* Header */}
        <div className="notification-drawer-header">
          <div className="header-title-box">
            <div className="bell-badge-wrap">
              <Bell size={22} className="text-primary" />
              {unreadCount > 0 && <span className="drawer-unread-badge">{unreadCount}</span>}
            </div>
            <div>
              <h3>Central de Notificações</h3>
              <div className="microservice-tag">
                <span className="live-dot pulse"></span>
                Microsserviço notification-service (Porta 8082)
              </div>
            </div>
          </div>
          <button className="btn-close-drawer" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        {/* Feedback alert */}
        {feedback && (
          <div className={`notification-feedback ${feedback.isError ? 'feedback-error' : 'feedback-success'}`}>
            {feedback.msg}
          </div>
        )}

        {/* Quick broadcast form */}
        <div className="broadcast-test-box">
          <div className="broadcast-title">
            <Radio size={14} className="text-danger" />
            <span>Emitir Alerta em Tempo Real (Broadcast)</span>
          </div>
          <form onSubmit={handleSendBroadcast} className="broadcast-form">
            <input 
              type="text" 
              placeholder="Ex: Atenção equipe, deploy em andamento!"
              value={broadcastMessage}
              onChange={e => setBroadcastMessage(e.target.value)}
              disabled={isSendingBroadcast}
            />
            <button type="submit" className="btn-send-broadcast" disabled={isSendingBroadcast || !broadcastMessage.trim()}>
              <Send size={14} />
              <span>Disparar</span>
            </button>
          </form>
        </div>

        {/* Actions & Filters */}
        <div className="notification-controls">
          <div className="notification-filter-tabs">
            <button 
              className={`filter-tab ${filter === 'ALL' ? 'active' : ''}`}
              onClick={() => setFilter('ALL')}
            >
              Todas ({notifications.length})
            </button>
            <button 
              className={`filter-tab ${filter === 'UNREAD' ? 'active' : ''}`}
              onClick={() => setFilter('UNREAD')}
            >
              Não lidas ({unreadCount})
            </button>
            <button 
              className={`filter-tab ${filter === 'ALERTS' ? 'active' : ''}`}
              onClick={() => setFilter('ALERTS')}
            >
              Alertas
            </button>
          </div>

          <div className="notification-actions">
            {unreadCount > 0 && (
              <button className="btn-mark-all-read" onClick={handleMarkAllRead} title="Marcar todas como lidas">
                <CheckCheck size={14} />
                <span>Marcar todas lidas</span>
              </button>
            )}
            <button className="btn-reload-notifications" onClick={loadNotifications} title="Recarregar" disabled={loading}>
              <RefreshCw size={14} className={loading ? 'spin' : ''} />
            </button>
          </div>
        </div>

        {/* Notifications List */}
        <div className="notifications-list-container">
          {loading && notifications.length === 0 ? (
            <div className="notification-empty-state">
              <RefreshCw size={32} className="spin text-muted" />
              <p>Consultando microsserviço de notificações...</p>
            </div>
          ) : filteredNotifications.length === 0 ? (
            <div className="notification-empty-state">
              <Bell size={40} className="empty-bell-icon" />
              <h4>Nenhuma notificação encontrada</h4>
              <p>Você está em dia com todos os alertas de tarefas!</p>
            </div>
          ) : (
            filteredNotifications.map(notification => (
              <div 
                key={notification.id} 
                className={`notification-item-card ${!notification.isRead ? 'unread' : 'read'} ${notification.type === 'HIGH_PRIORITY_ALERT' ? 'high-alert' : ''}`}
              >
                <div className="notification-icon-col">
                  {getTypeIcon(notification.type)}
                </div>
                <div className="notification-content-col">
                  <div className="notification-item-top">
                    <span className="notification-type-badge">
                      {notification.typeDescription || notification.type}
                    </span>
                    <span className="notification-time">
                      {formatTimestamp(notification.createdAt)}
                    </span>
                  </div>

                  {notification.taskTitle && (
                    <div className="notification-task-link">
                      Tarefa: <strong>{notification.taskTitle}</strong>
                    </div>
                  )}

                  <p className="notification-message-text">{notification.message}</p>

                  <div className="notification-item-bottom">
                    <span className="notification-meta-tag">
                      Canal: {notification.channelLabel || notification.channel}
                    </span>
                    <span className="notification-meta-tag">
                      Para: {notification.recipient}
                    </span>

                    {!notification.isRead && (
                      <button 
                        className="btn-mark-single-read" 
                        onClick={(e) => handleMarkAsRead(notification.id, e)}
                        title="Marcar como lida"
                      >
                        <Check size={13} />
                        <span>Marcar como lida</span>
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer info */}
        <div className="notification-drawer-footer">
          <span>Persistência isolada: <code>H2 notificationdb</code></span>
          <span className="api-endpoint-badge">REST: <code>/api/notifications</code></span>
        </div>
      </div>
    </div>
  );
}
