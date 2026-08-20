import React, { useState, useEffect } from 'react';
import { X, Save, User } from 'lucide-react';

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
          <h2>{taskToEdit ? 'Editar Tarefa' : 'Nova Tarefa'}</h2>
          <button className="btn-icon-only" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', color: '#ef4444', padding: '10px 14px', borderRadius: '8px', marginBottom: '16px', fontSize: '0.85rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Título da Tarefa *</label>
            <input
              type="text"
              className="form-input"
              placeholder="Ex: Implementar camada de Persistência JPA"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Descrição</label>
            <textarea
              className="form-textarea"
              rows={3}
              placeholder="Detalhes da modelagem, integridade ou requisitos..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Categoria (Relacionamento JPA)</label>
              <select className="form-select" value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
                <option value="">Sem Categoria</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Prioridade</label>
              <select className="form-select" value={priority} onChange={(e) => setPriority(e.target.value)}>
                <option value="LOW">Baixa</option>
                <option value="MEDIUM">Média</option>
                <option value="HIGH">Alta</option>
                <option value="URGENT">Urgente</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Status</label>
              <select className="form-select" value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="PENDING">Pendente</option>
                <option value="IN_PROGRESS">Em Progresso</option>
                <option value="COMPLETED">Concluída</option>
                <option value="CANCELLED">Cancelada</option>
              </select>
            </div>

            <div className="form-group">
              <label>Data de Entrega</label>
              <input
                type="datetime-local"
                className="form-input"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <label style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <User size={14} />
              <span>Autor da Modificação (Rastreabilidade / Auditoria)</span>
            </label>
            <input
              type="text"
              className="form-input"
              placeholder="Nome do usuário ou agente"
              value={updatedBy}
              onChange={(e) => setUpdatedBy(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
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
