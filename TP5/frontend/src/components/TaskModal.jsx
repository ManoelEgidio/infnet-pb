import React, { useState, useEffect } from 'react';
import { X, Save, User, Tag, AlertTriangle, CheckCircle2, Calendar } from 'lucide-react';
import CustomSelect from './CustomSelect';

const priorityOptions = [
  { value: 'LOW', label: 'Baixa', dotColor: '#94a3b8' },
  { value: 'MEDIUM', label: 'Média', dotColor: '#3b82f6' },
  { value: 'HIGH', label: 'Alta', dotColor: '#f59e0b' },
  { value: 'URGENT', label: 'Urgente (Notificação Imediata)', dotColor: '#ef4444' }
];

const statusOptions = [
  { value: 'PENDING', label: 'Pendente', dotColor: '#f59e0b' },
  { value: 'IN_PROGRESS', label: 'Em Progresso', dotColor: '#3b82f6' },
  { value: 'COMPLETED', label: 'Concluída', dotColor: '#10b981' },
  { value: 'CANCELLED', label: 'Cancelada', dotColor: '#64748b' }
];

export default function TaskModal({ isOpen, onClose, onSave, taskToEdit, categories = [] }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState('MEDIUM');
  const [status, setStatus] = useState('PENDING');
  const [categoryId, setCategoryId] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [updatedBy, setUpdatedBy] = useState('Manoel Egidio');
  const [error, setError] = useState('');

  useEffect(() => {
    if (taskToEdit) {
      setTitle(taskToEdit.title || '');
      setDescription(taskToEdit.description || '');
      setPriority(taskToEdit.priority || 'MEDIUM');
      setStatus(taskToEdit.status || 'PENDING');
      setCategoryId(taskToEdit.category?.id ? String(taskToEdit.category.id) : '');
      setDueDate(taskToEdit.dueDate ? taskToEdit.dueDate.substring(0, 16) : '');
      setUpdatedBy('Manoel Egidio');
    } else {
      setTitle('');
      setDescription('');
      setPriority('MEDIUM');
      setStatus('PENDING');
      setCategoryId(categories.length > 0 ? String(categories[0].id) : '');
      setDueDate('');
      setUpdatedBy('Manoel Egidio');
    }
    setError('');
  }, [taskToEdit, isOpen, categories]);

  if (!isOpen) return null;

  const categoryOptions = [
    { value: '', label: 'Sem Categoria', dotColor: '#64748b' },
    ...categories.map((c) => ({
      value: String(c.id),
      label: c.name,
      dotColor: c.colorCode || '#6366f1'
    }))
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title.trim() || title.trim().length < 3) {
      setError('O título deve conter pelo menos 3 caracteres.');
      return;
    }

    const payload = {
      title,
      description,
      priority,
      status,
      categoryId: categoryId ? Number(categoryId) : null,
      dueDate: dueDate ? new Date(dueDate).toISOString() : null,
      updatedBy: updatedBy || 'Manoel Egidio'
    };

    onSave(payload, taskToEdit ? taskToEdit.id : null);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h2 className="modal-title">{taskToEdit ? 'Editar Tarefa' : 'Nova Tarefa'}</h2>
            <p className="modal-subtitle">
              {taskToEdit
                ? `Atualizando tarefa #${taskToEdit.id} — Notificação será propagada via Feign`
                : 'Crie uma nova tarefa integrada ao ecossistema distribuído'}
            </p>
          </div>
          <button className="btn-icon-only modal-close-btn" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        {error && (
          <div className="modal-alert-error">
            <AlertTriangle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label className="form-label">Título da Tarefa *</label>
            <input
              type="text"
              className="form-input"
              placeholder="Ex: Refatorar microsserviço de mensageria assíncrona"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Descrição detalhada</label>
            <textarea
              className="form-textarea"
              rows={3}
              placeholder="Descreva o escopo, dependências e critérios de aceitação..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                <Tag size={14} />
                <span>Categoria (JPA Relacionamento)</span>
              </label>
              <CustomSelect
                value={categoryId}
                onChange={setCategoryId}
                options={categoryOptions}
                placeholder="Selecione uma categoria"
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                <AlertTriangle size={14} />
                <span>Prioridade</span>
              </label>
              <CustomSelect
                value={priority}
                onChange={setPriority}
                options={priorityOptions}
                placeholder="Selecione a prioridade"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                <CheckCircle2 size={14} />
                <span>Status Atual</span>
              </label>
              <CustomSelect
                value={status}
                onChange={setStatus}
                options={statusOptions}
                placeholder="Selecione o status"
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                <Calendar size={14} />
                <span>Data de Conclusão Prevista</span>
              </label>
              <input
                type="datetime-local"
                className="form-input"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">
              <User size={14} />
              <span>Autor da Modificação (Auditoria &amp; Rastreabilidade)</span>
            </label>
            <input
              type="text"
              className="form-input"
              placeholder="Nome do operador ou desenvolvedor"
              value={updatedBy}
              onChange={(e) => setUpdatedBy(e.target.value)}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary">
              <Save size={16} />
              <span>Salvar Tarefa</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
