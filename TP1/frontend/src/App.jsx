import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import DashboardMetrics from './components/DashboardMetrics';
import TaskCard from './components/TaskCard';
import TaskModal from './components/TaskModal';
import { fetchTasks, fetchMetrics, createTask, updateTask, updateTaskStatus, deleteTask } from './services/api';
import { Search, Filter, RefreshCw, AlertCircle, Sparkles } from 'lucide-react';

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters & Search
  const [activeStatusTab, setActiveStatusTab] = useState('ALL');
  const [selectedPriority, setSelectedPriority] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [taskToEdit, setTaskToEdit] = useState(null);
  const [toastMessage, setToastMessage] = useState('');

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const statusParam = activeStatusTab === 'ALL' ? '' : activeStatusTab;
      const [tasksData, metricsData] = await Promise.all([
        fetchTasks(statusParam, selectedPriority),
        fetchMetrics()
      ]);
      setTasks(tasksData);
      setMetrics(metricsData);
    } catch (err) {
      console.error(err);
      setError('Não foi possível conectar ao backend Spring Boot (http://localhost:8080). Certifique-se de que a API está rodando.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [activeStatusTab, selectedPriority]);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const handleSaveTask = async (taskPayload, id) => {
    try {
      if (id) {
        await updateTask(id, taskPayload);
        showToast('Tarefa atualizada com sucesso!');
      } else {
        await createTask(taskPayload);
        showToast('Nova tarefa criada com sucesso!');
      }
      setIsModalOpen(false);
      setTaskToEdit(null);
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao salvar tarefa.');
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await updateTaskStatus(id, newStatus);
      showToast('Status atualizado!');
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao atualizar status.');
    }
  };

  const handleDeleteTask = async (id) => {
    if (window.confirm('Tem certeza de que deseja excluir esta tarefa?')) {
      try {
        await deleteTask(id);
        showToast('Tarefa excluída com sucesso!');
        loadData();
      } catch (err) {
        alert(err.message || 'Erro ao excluir tarefa.');
      }
    }
  };

  const handleOpenEdit = (task) => {
    setTaskToEdit(task);
    setIsModalOpen(true);
  };

  const handleOpenNew = () => {
    setTaskToEdit(null);
    setIsModalOpen(true);
  };

  const filteredTasks = tasks.filter(t => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return t.title.toLowerCase().includes(q) || (t.description && t.description.toLowerCase().includes(q));
  });

  return (
    <div className="app-container">
      <Navbar onOpenNewTaskModal={handleOpenNew} />

      <DashboardMetrics metrics={metrics} />

      <div className="filter-bar">
        <div className="search-input-group">
          <Search size={18} color="#94a3b8" />
          <input
            type="text"
            placeholder="Pesquisar tarefas por título ou descrição..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="filter-pills">
          <button
            className={`pill ${activeStatusTab === 'ALL' ? 'active' : ''}`}
            onClick={() => setActiveStatusTab('ALL')}
          >
            Todas
          </button>
          <button
            className={`pill ${activeStatusTab === 'PENDING' ? 'active' : ''}`}
            onClick={() => setActiveStatusTab('PENDING')}
          >
            Pendentes
          </button>
          <button
            className={`pill ${activeStatusTab === 'IN_PROGRESS' ? 'active' : ''}`}
            onClick={() => setActiveStatusTab('IN_PROGRESS')}
          >
            Em Progresso
          </button>
          <button
            className={`pill ${activeStatusTab === 'COMPLETED' ? 'active' : ''}`}
            onClick={() => setActiveStatusTab('COMPLETED')}
          >
            Concluídas
          </button>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <Filter size={16} color="#94a3b8" />
          <select
            className="select-input"
            value={selectedPriority}
            onChange={(e) => setSelectedPriority(e.target.value)}
          >
            <option value="">Todas as Prioridades</option>
            <option value="LOW">Baixa</option>
            <option value="MEDIUM">Média</option>
            <option value="HIGH">Alta</option>
            <option value="URGENT">Urgente</option>
          </select>

          <button className="btn-icon-only" title="Recarregar dados" onClick={loadData}>
            <RefreshCw size={18} />
          </button>
        </div>
      </div>

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '12px', padding: '20px', display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '30px' }}>
          <AlertCircle size={28} color="#ef4444" />
          <div>
            <h4 style={{ color: '#ef4444', margin: 0 }}>Conexão com o Backend Spring Boot</h4>
            <p style={{ color: '#94a3b8', fontSize: '0.9rem', margin: 0 }}>{error}</p>
          </div>
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0', color: '#94a3b8' }}>
          <RefreshCw className="animate-spin" size={32} style={{ margin: '0 auto 12px' }} />
          <p>Carregando tarefas do servidor...</p>
        </div>
      ) : filteredTasks.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📋</div>
          <h3>Nenhuma tarefa encontrada</h3>
          <p style={{ marginTop: '6px' }}>Crie uma nova tarefa ou altere seus filtros de busca.</p>
          <button className="btn btn-primary" style={{ marginTop: '16px' }} onClick={handleOpenNew}>
            <Sparkles size={16} />
            <span>Criar Primeira Tarefa</span>
          </button>
        </div>
      ) : (
        <div className="tasks-grid">
          {filteredTasks.map((t) => (
            <TaskCard
              key={t.id}
              task={t}
              onEdit={handleOpenEdit}
              onDelete={handleDeleteTask}
              onStatusChange={handleStatusChange}
            />
          ))}
        </div>
      )}

      <TaskModal
        isOpen={isModalOpen}
        onClose={() => { setIsModalOpen(false); setTaskToEdit(null); }}
        onSave={handleSaveTask}
        taskToEdit={taskToEdit}
      />

      {toastMessage && (
        <div className="toast">
          <Sparkles size={18} color="#6366f1" />
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
}
