import React, { useEffect } from 'react';
import { Bell, CheckCircle2, AlertTriangle, X } from 'lucide-react';

export default function NotificationToast({ toast, onClose, onOpenCenter }) {
  useEffect(() => {
    if (toast) {
      const timer = setTimeout(() => {
        onClose();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [toast, onClose]);

  if (!toast) return null;

  return (
    <div className={`notification-toast ${toast.type || 'info'}`} onClick={onOpenCenter}>
      <div className="toast-icon">
        {toast.type === 'alert' ? (
          <AlertTriangle size={20} className="text-amber-400" />
        ) : toast.type === 'success' ? (
          <CheckCircle2 size={20} className="text-emerald-400" />
        ) : (
          <Bell size={20} className="text-blue-400" />
        )}
      </div>
      <div className="toast-body">
        <div className="toast-source">Microsserviço de Notificações (8082)</div>
        <div className="toast-title">{toast.title}</div>
        <div className="toast-message">{toast.message}</div>
      </div>
      <button 
        className="toast-close-btn" 
        onClick={(e) => {
          e.stopPropagation();
          onClose();
        }}
        aria-label="Fechar"
      >
        <X size={16} />
      </button>
    </div>
  );
}
