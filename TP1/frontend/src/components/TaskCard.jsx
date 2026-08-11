import React from 'react';
import { Calendar, Edit3, Trash2, CheckCircle, Clock, PlayCircle } from 'lucide-react';

const priorityLabels = {
  LOW: { label: 'Baixa', class: 'badge-low' },
  MEDIUM: { label: 'Média', class: 'badge-medium' },
  HIGH: { label: 'Alta', class: 'badge-high' },
  URGENT: { label: 'Urgente', class: 'badge-urgent' }
};

const statusLabels = {
  PENDING: { label: 'Pendente', class: 'badge-pending' },
  IN_PROGRESS: { label: 'Em Progresso', class: 'badge-in_progress' },
  COMPLETED: { label: 'Concluída', class: 'badge-completed' },
  CANCELLED: { label: 'Cancelada', class: 'badge-low' }
};

export default function TaskCard({ task, onEdit, onDelete, onStatusChange }) {
  const priorityInfo = priorityLabels[task.priority] || priorityLabels.MEDIUM;
  const statusInfo = statusLabels[task.status] || statusLabels.PENDING;

  const formattedDate = task.dueDate
    ? new Date(task.dueDate).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })
    : 'Sem data';

  return (
    <div className="task-card">
      <div>
        <div className="task-header">
          <h3 className="task-title">{task.title}</h3>
          <span className={`badge ${priorityInfo.class}`}>{priorityInfo.label}</span>
        </div>
        <p className="task-desc" style={{ marginTop: '8px' }}>
          {task.description || 'Sem descrição.'}
        </p>
      </div>

      <div className="task-meta">
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span className={`badge ${statusInfo.class}`}>{statusInfo.label}</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.8rem', color: '#94a3b8' }}>
            <Calendar size={13} />
            <span>{formattedDate}</span>
          </div>
        </div>

        <div className="task-actions">
          {task.status !== 'COMPLETED' && (
            <button
              className="btn-icon-only"
              title="Marcar como Concluída"
              onClick={() => onStatusChange(task.id, 'COMPLETED')}
              style={{ color: '#10b981' }}
            >
              <CheckCircle size={18} />
            </button>
          )}

          {task.status === 'PENDING' && (
            <button
              className="btn-icon-only"
              title="Iniciar Tarefa"
              onClick={() => onStatusChange(task.id, 'IN_PROGRESS')}
              style={{ color: '#3b82f6' }}
            >
              <PlayCircle size={18} />
            </button>
          )}

          <button className="btn-icon-only" title="Editar Tarefa" onClick={() => onEdit(task)}>
            <Edit3 size={16} />
          </button>

          <button
            className="btn-icon-only"
            title="Excluir Tarefa"
            onClick={() => onDelete(task.id)}
            style={{ color: '#ef4444' }}
          >
            <Trash2 size={16} />
          </button>
        </div>
      </div>
    </div>
  );
}
