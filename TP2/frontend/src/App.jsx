import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import DashboardMetrics from './components/DashboardMetrics';
import TaskCard from './components/TaskCard';
import TaskModal from './components/TaskModal';
import HistoryModal from './components/HistoryModal';
import { 
  fetchTasks, 
  fetchMetrics, 
  fetchCategories, 
  fetchTaskHistory, 
  createTask, 
  updateTask, 
  updateTaskStatus, 
  deleteTask 
} from './services/api';
import { Search, Filter, RefreshCw, AlertCircle, Sparkles, Database, Plus, History } from 'lucide-react';

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters & Search
  const [activeStatusTab, setActiveStatusTab] = useState('ALL');
  const [selectedPriority, setSelectedPriority] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  // Modals State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [taskToEdit, setTaskToEdit] = useState(null);
  
  // History Modal State
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [selectedTaskHistory, setSelectedTaskHistory] = useState(null);
  const [historyItems, setHistoryItems] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  const [toastMessage, setToastMessage] = useState('');

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const statusParam = activeStatusTab === 'ALL' ? '' : activeStatusTab;
      const [tasksData, metricsData, categoriesData] = await Promise.all([
        fetchTasks(statusParam, selectedPriority, selectedCategory),
        fetchMetrics(),
        fetchCategories()
      ]);
      setTasks(tasksData);
      setMetrics(metricsData);
      setCategories(categoriesData);
    } catch (err) {
      console.error(err);
      setError('Não foi possível conectar ao backend Spring Boot (http://localhost:8080). Certifique-se de que a API está rodando.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [activeStatusTab, selectedPriority, selectedCategory]);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const handleSaveTask = async (taskPayload, id) => {
    try {
      if (id) {
        await updateTask(id, taskPayload);
        showToast('Tarefa e auditoria atualizadas com sucesso!');
      } else {
        await createTask(taskPayload);
        showToast('Nova tarefa persistida com histórico de criação!');
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
      await updateTaskStatus(id, newStatus, 'Interface Web');
      showToast('Status e trilha de auditoria atualizados!');
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao atualizar status.');
    }
  };

  const handleDeleteTask = async (id) => {
    if (window.confirm('Tem certeza de que deseja excluir esta tarefa? (Ação será registrada na auditoria)')) {
      try {
        await deleteTask(id, 'Usuário Administrador');
        showToast('Tarefa excluída com registro de auditoria!');
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

  const handleViewHistory = async (task) => {
    setSelectedTaskHistory(task);
    setIsHistoryOpen(true);
    try {
      setHistoryLoading(true);
      const historyData = await fetchTaskHistory(task.id);
      setHistoryItems(historyData);
    } catch (err) {
      alert('Erro ao carregar histórico: ' + err.message);
    } finally {
      setHistoryLoading(false);
    }
  };

  const filteredTasks = tasks.filter((t) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      (t.title && t.title.toLowerCase().includes(q)) ||
      (t.description && t.description.toLowerCase().includes(q)) ||
      (t.category?.name && t.category.name.toLowerCase().includes(q))
    );
  });

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col font-sans">
      <Navbar onNewTask={handleOpenNew} />

      {toastMessage && (
        <div className="toast animate-in fade-in slide-in-from-bottom-5">
          <Sparkles size={16} />
          <span>{toastMessage}</span>
        </div>
      )}

      <main className="main-content flex-1 max-w-7xl mx-auto w-full p-6 space-y-6">
        {/* Banner de Informações de Persistência TP2 */}
        <div className="bg-gradient-to-r from-indigo-900/50 to-blue-900/40 border border-indigo-500/30 p-5 rounded-2xl flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center space-x-3">
            <div className="p-3 bg-indigo-600/30 border border-indigo-400/30 rounded-xl text-indigo-400">
              <Database className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                TP2: Camada de Persistência Avançada com JPA & Histórico
              </h2>
              <p className="text-xs text-slate-300">
                Spring Data JPA • Mapeamento ORM • Relacionamentos • Auditoria de Mudanças • Consultas JPQL Customizadas
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <button onClick={handleOpenNew} className="btn btn-primary text-sm flex items-center gap-1.5">
              <Plus size={16} />
              <span>Nova Tarefa</span>
            </button>
            <button onClick={loadData} className="btn btn-secondary text-sm flex items-center gap-1.5" title="Recarregar dados">
              <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
              <span>Atualizar</span>
            </button>
          </div>
        </div>

        {/* Métricas de Performance e Domínio */}
        <DashboardMetrics metrics={metrics} />

        {/* Filtros e Busca */}
        <div className="controls-panel">
          <div className="search-bar">
            <Search size={18} color="#94a3b8" />
            <input
              type="text"
              placeholder="Buscar por título, descrição ou categoria..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div className="filter-group">
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#94a3b8', fontSize: '0.875rem' }}>
              <Filter size={16} />
              <span>Categoria:</span>
            </div>
            <select
              className="filter-select"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              <option value="">Todas as Categorias</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#94a3b8', fontSize: '0.875rem' }}>
              <Filter size={16} />
              <span>Prioridade:</span>
            </div>
            <select
              className="filter-select"
              value={selectedPriority}
              onChange={(e) => setSelectedPriority(e.target.value)}
            >
              <option value="">Todas</option>
              <option value="LOW">Baixa</option>
              <option value="MEDIUM">Média</option>
              <option value="HIGH">Alta</option>
              <option value="URGENT">Urgente</option>
            </select>
          </div>
        </div>

        {/* Status Tabs */}
        <div className="tabs-container">
          {[
            { id: 'ALL', label: 'Todas' },
            { id: 'PENDING', label: 'Pendentes' },
            { id: 'IN_PROGRESS', label: 'Em Progresso' },
            { id: 'COMPLETED', label: 'Concluídas' }
          ].map((tab) => (
            <button
              key={tab.id}
              className={`tab-btn ${activeStatusTab === tab.id ? 'active' : ''}`}
              onClick={() => setActiveStatusTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Mensagem de Erro */}
        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#fca5a5', padding: '16px', borderRadius: '12px', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <AlertCircle size={24} color="#ef4444" />
            <div>
              <h4 style={{ fontWeight: '600', color: '#fff' }}>Falha na Conexão com o Backend</h4>
              <p style={{ fontSize: '0.85rem' }}>{error}</p>
            </div>
          </div>
        )}

        {/* Lista de Tarefas */}
        {loading ? (
          <div style={{ textAlign: 'center', padding: '60px', color: '#94a3b8' }}>
            <RefreshCw size={32} className="animate-spin" style={{ margin: '0 auto 12px' }} />
            <p>Consultando banco de dados JPA...</p>
          </div>
        ) : filteredTasks.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '60px', background: '#1e293b', borderRadius: '16px', border: '1px dashed #334155' }}>
            <p style={{ color: '#94a3b8', fontSize: '1.1rem' }}>Nenhuma tarefa encontrada com os filtros selecionados.</p>
            <button className="btn btn-primary" onClick={handleOpenNew} style={{ marginTop: '16px' }}>
              Criar Nova Tarefa
            </button>
          </div>
        ) : (
          <div className="tasks-grid">
            {filteredTasks.map((task) => (
              <TaskCard
                key={task.id}
                task={task}
                onEdit={handleOpenEdit}
                onDelete={handleDeleteTask}
                onStatusChange={handleStatusChange}
                onViewHistory={handleViewHistory}
              />
            ))}
          </div>
        )}
      </main>

      {/* Modal de Criação / Edição */}
      <TaskModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setTaskToEdit(null);
        }}
        onSave={handleSaveTask}
        taskToEdit={taskToEdit}
        categories={categories}
      />

      {/* Modal de Histórico de Auditoria */}
      <HistoryModal
        isOpen={isHistoryOpen}
        onClose={() => {
          setIsHistoryOpen(false);
          setSelectedTaskHistory(null);
        }}
        task={selectedTaskHistory}
        history={historyItems}
        loading={historyLoading}
      />
    </div>
  );
}
