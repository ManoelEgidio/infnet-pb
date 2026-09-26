import React from 'react';
import { CheckCircle2, Clock, AlertTriangle, ListTodo, Flame } from 'lucide-react';

export default function DashboardMetrics({ metrics }) {
  if (!metrics) return null;

  const { totalTasks, pendingTasks, inProgressTasks, completedTasks, urgentTasks } = metrics;
  const completionPercentage = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;

  return (
    <div className="metrics-grid">
      <div className="metric-card">
        <div className="metric-header">
          <span className="metric-title">Total de Tarefas</span>
          <div className="metric-icon" style={{ background: 'rgba(99, 102, 241, 0.15)', color: '#6366f1' }}>
            <ListTodo size={20} />
          </div>
        </div>
        <div className="metric-value">{totalTasks}</div>
        <div className="progress-bar-bg">
          <div className="progress-bar-fill" style={{ width: `${completionPercentage}%` }}></div>
        </div>
      </div>

      <div className="metric-card">
        <div className="metric-header">
          <span className="metric-title">Pendentes</span>
          <div className="metric-icon" style={{ background: 'rgba(245, 158, 11, 0.15)', color: '#f59e0b' }}>
            <Clock size={20} />
          </div>
        </div>
        <div className="metric-value">{pendingTasks}</div>
      </div>

      <div className="metric-card">
        <div className="metric-header">
          <span className="metric-title">Em Progresso</span>
          <div className="metric-icon" style={{ background: 'rgba(59, 130, 246, 0.15)', color: '#3b82f6' }}>
            <AlertTriangle size={20} />
          </div>
        </div>
        <div className="metric-value">{inProgressTasks}</div>
      </div>

      <div className="metric-card">
        <div className="metric-header">
          <span className="metric-title">Concluídas</span>
          <div className="metric-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#10b981' }}>
            <CheckCircle2 size={20} />
          </div>
        </div>
        <div className="metric-value">{completedTasks}</div>
      </div>

      <div className="metric-card">
        <div className="metric-header">
          <span className="metric-title">Urgentes</span>
          <div className="metric-icon" style={{ background: 'rgba(239, 68, 68, 0.15)', color: '#ef4444' }}>
            <Flame size={20} />
          </div>
        </div>
        <div className="metric-value">{urgentTasks}</div>
      </div>
    </div>
  );
}
