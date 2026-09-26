import React from 'react';
import { Layers, Plus, Bell, Cpu } from 'lucide-react';
import ServiceStatusIndicator from './ServiceStatusIndicator';

export default function Navbar({ 
  onOpenNewTaskModal, 
  onOpenNotificationCenter, 
  onOpenEdaSimulator,
  unreadNotificationCount = 0 
}) {
  return (
    <header className="navbar">
      <div className="brand">
        <div className="brand-icon">
          <Layers size={24} />
        </div>
        <div>
          <h1 className="brand-title">TaskManager PB</h1>
          <p className="brand-subtitle">TP4: Arquitetura Orientada a Eventos (EDA) com RabbitMQ Message Broker</p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <ServiceStatusIndicator />

        {/* Botão Simulador RabbitMQ EDA */}
        <button
          className="btn btn-secondary"
          onClick={onOpenEdaSimulator}
          title="Abrir Simulador de Cenários RabbitMQ (Alta Vazão, DLQ, Fanout)"
          style={{ gap: '6px', padding: '9px 15px' }}
        >
          <Cpu size={16} style={{ color: '#818cf8' }} />
          <span>Simulador EDA</span>
        </button>

        {/* Botão da Central de Notificações */}
        <button 
          className="btn-notification-trigger" 
          onClick={onOpenNotificationCenter}
          title="Abrir Central de Notificações (Consumidor RabbitMQ)"
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
