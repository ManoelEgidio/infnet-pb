import React from 'react';
import { Clock, User, ArrowRight, ShieldCheck, X } from 'lucide-react';

export default function HistoryModal({ isOpen, onClose, task, history = [], loading }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content modal-history-content" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="modal-header">
          <div className="modal-title-wrap">
            <div className="modal-header-icon-box audit">
              <ShieldCheck size={22} />
            </div>
            <div>
              <h2 className="modal-title">Histórico de Alterações</h2>
              <p className="modal-subtitle">
                Tarefa #{task?.id}: {task?.title || 'Detalhes da Tarefa'}
              </p>
            </div>
          </div>
          <button className="btn-icon-only modal-close-btn" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        {/* Content */}
        <div className="modal-scroll-body">
          {loading ? (
            <div className="modal-empty-state">
              <div className="spinner-glow"></div>
              <p>Carregando logs de auditoria JPA do banco de dados...</p>
            </div>
          ) : history.length === 0 ? (
            <div className="modal-empty-state">
              <Clock size={36} className="modal-empty-icon" />
              <h4>Nenhum registro de auditoria</h4>
              <p>Esta tarefa ainda não possui alterações registradas na base de dados.</p>
            </div>
          ) : (
            <div className="history-timeline">
              {history.map((item) => (
                <div key={item.id} className="history-item">
                  <div className="timeline-connector">
                    <div className="timeline-dot"></div>
                    <div className="timeline-line"></div>
                  </div>

                  <div className="history-card">
                    <div className="history-card-header">
                      <span className="history-badge">
                        {item.actionDescription || item.actionType}
                      </span>
                      <div className="history-time">
                        <Clock size={13} />
                        <span>
                          {item.changedAt
                            ? new Date(item.changedAt).toLocaleString('pt-BR')
                            : 'Data não registrada'}
                        </span>
                      </div>
                    </div>

                    <p className="history-details">{item.details}</p>

                    {item.oldValue && item.newValue && (
                      <div className="history-diff-box">
                        <span className="diff-old">{item.oldValue}</span>
                        <ArrowRight size={14} className="diff-arrow" />
                        <span className="diff-new">{item.newValue}</span>
                      </div>
                    )}

                    <div className="history-author">
                      <User size={13} />
                      <span>
                        Modificado por: <strong>{item.changedBy || 'Sistema'}</strong>
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="modal-actions" style={{ marginTop: '20px' }}>
          <button onClick={onClose} className="btn btn-secondary">
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
