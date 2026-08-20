import React from 'react';
import { Clock, User, ArrowRight, ShieldCheck, X } from 'lucide-react';

export default function HistoryModal({ isOpen, onClose, task, history, loading }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[85vh] flex flex-col overflow-hidden border border-slate-100 animate-in fade-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="p-6 bg-slate-900 text-white flex justify-between items-center">
          <div>
            <div className="flex items-center space-x-2 text-indigo-400 text-sm font-medium mb-1">
              <ShieldCheck className="w-4 h-4" />
              <span>Auditoria e Rastreabilidade (JPA Persistência)</span>
            </div>
            <h3 className="text-xl font-bold text-white">Histórico de Alterações</h3>
            <p className="text-slate-400 text-xs mt-0.5 truncate max-w-md">
              Tarefa #{task?.id}: {task?.title}
            </p>
          </div>
          <button 
            onClick={onClose}
            className="text-slate-400 hover:text-white p-2 rounded-lg hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto flex-1 space-y-4">
          {loading ? (
            <div className="flex flex-col items-center justify-center py-12 text-slate-500">
              <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin mb-3"></div>
              <p className="text-sm">Carregando logs de auditoria do banco de dados...</p>
            </div>
          ) : history.length === 0 ? (
            <div className="text-center py-10 text-slate-500">
              <Clock className="w-10 h-10 mx-auto text-slate-300 mb-2" />
              <p className="text-sm">Nenhum histórico registrado para esta tarefa ainda.</p>
            </div>
          ) : (
            <div className="relative border-l-2 border-indigo-200 ml-3 pl-6 space-y-6">
              {history.map((item) => (
                <div key={item.id} className="relative group">
                  {/* Dot */}
                  <div className="absolute -left-[31px] top-1.5 w-3.5 h-3.5 rounded-full bg-indigo-600 border-2 border-white shadow"></div>
                  
                  <div className="bg-slate-50 hover:bg-slate-100/80 p-4 rounded-xl border border-slate-200/80 transition shadow-sm">
                    <div className="flex flex-wrap items-center justify-between gap-2 mb-2">
                      <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-indigo-100 text-indigo-700">
                        {item.actionDescription || item.actionType}
                      </span>
                      <div className="flex items-center text-xs text-slate-400 space-x-1">
                        <Clock className="w-3.5 h-3.5" />
                        <span>{item.changedAt ? new Date(item.changedAt).toLocaleString('pt-BR') : 'Data não registrada'}</span>
                      </div>
                    </div>

                    <p className="text-sm text-slate-700 font-medium">{item.details}</p>

                    {item.oldValue && item.newValue && (
                      <div className="mt-2 flex items-center text-xs bg-white p-2 rounded-lg border border-slate-200 text-slate-600">
                        <span className="text-red-500 font-semibold line-through mr-2">{item.oldValue}</span>
                        <ArrowRight className="w-3.5 h-3.5 text-slate-400 mr-2" />
                        <span className="text-emerald-600 font-semibold">{item.newValue}</span>
                      </div>
                    )}

                    <div className="mt-2.5 flex items-center text-xs text-slate-500 font-medium">
                      <User className="w-3.5 h-3.5 mr-1 text-slate-400" />
                      <span>Alterado por: <strong className="text-slate-700">{item.changedBy}</strong></span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 bg-slate-50 border-t border-slate-200 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 bg-slate-800 hover:bg-slate-900 text-white text-sm font-medium rounded-lg transition"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
