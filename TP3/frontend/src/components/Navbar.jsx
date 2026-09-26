import React from 'react';
import { Layers, Plus, Bell } from 'lucide-react';
import ServiceStatusIndicator from './ServiceStatusIndicator';

export default function Navbar({ onOpenNewTaskModal, onOpenNotificationCenter, unreadNotificationCount = 0 }) {
  return (
    <header className="navbar">
      <div className="brand">
        <div className="brand-icon">
          <Layers size={24} />
        </div>
        <div>
          <h1 className="brand-title">TaskManager PB</h1>
          <p className="brand-subtitle">TP3: Arquitetura Distribuída &amp; Microsserviço com Spring Cloud</p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
        <ServiceStatusIndicator />

        {/* Botão da Central de Notificações */}
        <button 
          className="btn-notification-trigger" 
          onClick={onOpenNotificationCenter}
          title="Abrir Central de Notificações (Microsserviço Porta 8082)"
          aria-label="Notificações"
        >
          <Bell size={20} />
          {unreadNotificationCount > 0 && (
            <span className="notification-counter-badge pulse">
              {unreadNotificationCount > 99 ? '99+' : unreadNotificationCount}
            </span>
          )}
        </button>

        <button className="btn btn-primary" onClick={onOpenNewTaskModal}>
          <Plus size={18} />
          <span>Nova Tarefa</span>
        </button>
      </div>
    </header>
  );
}
