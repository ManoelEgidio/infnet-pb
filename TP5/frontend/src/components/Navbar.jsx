import React from 'react';
import { Layers, Plus, Bell, Cpu, Activity } from 'lucide-react';
import ServiceStatusIndicator from './ServiceStatusIndicator';

export default function Navbar({ 
  onOpenNewTaskModal, 
  onOpenNotificationCenter, 
  onOpenEdaSimulator,
  onOpenObservability,
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
          <p className="brand-subtitle">TP5: Implantação e Manutenção em Produção (Docker, Kubernetes & Observabilidade)</p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <ServiceStatusIndicator />

        {/* Botão Painel de Observabilidade TP5 */}
        <button
          className="btn btn-secondary"
          onClick={onOpenObservability}
          title="Abrir Painel de Observabilidade, Probes K8s e Tracing"
          style={{ gap: '6px', padding: '9px 15px' }}
        >
          <Activity size={16} style={{ color: '#38bdf8' }} />
          <span>Observabilidade</span>
        </button>

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
