import React from 'react';
import { Calendar, Edit3, Trash2, CheckCircle, PlayCircle, History, Bell } from 'lucide-react';

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

export default function TaskCard({ 
  task, 
  onEdit, 
  onDelete, 
  onStatusChange, 
  onViewHistory,
  onViewTaskNotifications 
}) {
  const priorityInfo = priorityLabels[task.priority] || priorityLabels.MEDIUM;
  const statusInfo = statusLabels[task.status] || statusLabels.PENDING;

  const formattedDate = task.dueDate
    ? new Date(task.dueDate).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })
    : 'Sem prazo definido';

  return (
    <div className="task-card">
      <div className="task-card-body">
        <div className="task-header">
          <h3 className="task-title">{task.title}</h3>
          <span className={`badge ${priorityInfo.class}`}>{priorityInfo.label}</span>
        </div>

        <p className="task-desc">
          {task.description || 'Nenhum detalhe adicional fornecido para esta tarefa.'}
        </p>
        
        {task.category && (
          <div className="task-category-tag">
            <span 
              className="category-color-dot" 
              style={{ backgroundColor: task.category.colorCode || '#6366f1' }} 
            />
            <span>{task.category.name}</span>
          </div>
        )}
      </div>

      <div className="task-meta">
        <div className="task-meta-left">
          <span className={`badge ${statusInfo.class}`}>{statusInfo.label}</span>
          <div className="task-due-info">
            <Calendar size={14} />
            <span>{formattedDate}</span>
          </div>
        </div>

        <div className="task-actions">
          {/* Botão de Notificações Distribuídas (Spring Cloud Feign) */}
          <button
            className="btn-icon-action alert-btn"
            title="Ver Notificações Distribuídas (Spring Cloud Feign)"
            onClick={() => onViewTaskNotifications(task)}
          >
            <Bell size={16} />
          </button>

          {/* Botão de Histórico / Auditoria */}
          <button
            className="btn-icon-action audit-btn"
            title="Ver Histórico de Auditoria JPA"
            onClick={() => onViewHistory(task)}
          >
            <History size={16} />
          </button>

          {task.status !== 'COMPLETED' && (
            <button
              className="btn-icon-action complete-btn"
              title="Marcar como Concluída (Emite Notificação)"
              onClick={() => onStatusChange(task.id, 'COMPLETED')}
            >
              <CheckCircle size={17} />
            </button>
          )}

          {task.status === 'PENDING' && (
            <button
              className="btn-icon-action start-btn"
              title="Iniciar Tarefa"
              onClick={() => onStatusChange(task.id, 'IN_PROGRESS')}
            >
              <PlayCircle size={17} />
            </button>
          )}

          <button 
            className="btn-icon-action edit-btn" 
            title="Editar Tarefa" 
            onClick={() => onEdit(task)}
          >
            <Edit3 size={15} />
          </button>

          <button
            className="btn-icon-action delete-btn"
            title="Excluir Tarefa"
            onClick={() => onDelete(task.id)}
          >
            <Trash2 size={15} />
          </button>
        </div>
      </div>
    </div>
  );
}
