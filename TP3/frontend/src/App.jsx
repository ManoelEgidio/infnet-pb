import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import DashboardMetrics from './components/DashboardMetrics';
import TaskCard from './components/TaskCard';
import TaskModal from './components/TaskModal';
import HistoryModal from './components/HistoryModal';
import NotificationCenter from './components/NotificationCenter';
import NotificationToast from './components/NotificationToast';
import TaskNotificationModal from './components/TaskNotificationModal';
import CustomSelect from './components/CustomSelect';
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
import { fetchNotificationSummary } from './services/notificationApi';
import { Search, Filter, RefreshCw, AlertCircle, Sparkles, Plus, Bell, Network, Layers } from 'lucide-react';

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

  // TP3 Microservice State
  const [isNotificationCenterOpen, setIsNotificationCenterOpen] = useState(false);
  const [unreadNotificationCount, setUnreadNotificationCount] = useState(0);
  const [activeToast, setActiveToast] = useState(null);
  const [taskForNotifications, setTaskForNotifications] = useState(null);
  const [isTaskNotificationsOpen, setIsTaskNotificationsOpen] = useState(false);

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
      setError('Não foi possível conectar ao backend Spring Boot (http://localhost:8080). Certifique-se de que os serviços estão rodando.');
    } finally {
      setLoading(false);
    }
  };

  const loadNotificationSummary = async () => {
    try {
      const summary = await fetchNotificationSummary();
      if (summary && summary.unreadCount !== undefined) {
        setUnreadNotificationCount(summary.unreadCount);
      }
    } catch {
      // Degradação graciosa enquanto notification-service inicializa
    }
  };

  useEffect(() => {
    loadData();
    loadNotificationSummary();

    const interval = setInterval(loadNotificationSummary, 10000);
    return () => clearInterval(interval);
  }, [activeStatusTab, selectedPriority, selectedCategory]);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const triggerMicroserviceToast = (title, message, type = 'info') => {
    setActiveToast({ title, message, type });
  };

  const handleSaveTask = async (taskPayload, id) => {
    try {
      if (id) {
        await updateTask(id, taskPayload);
        showToast('Tarefa atualizada no task-service!');
        triggerMicroserviceToast(
          'Evento de Atualização Emitido',
          `Alterações na tarefa enviadas ao notification-service via Feign.`,
          'info'
        );
      } else {
        await createTask(taskPayload);
        showToast('Nova tarefa persistida com sucesso!');
        const isUrgent = taskPayload.priority === 'URGENT' || taskPayload.priority === 'HIGH';
        triggerMicroserviceToast(
          isUrgent ? 'Alerta Crítico Emitido' : 'Nova Notificação Gerada',
          `Microsserviço de Notificações gravou evento para "${taskPayload.title}".`,
          isUrgent ? 'alert' : 'success'
        );
      }
      setIsModalOpen(false);
      setTaskToEdit(null);
      loadData();
      setTimeout(loadNotificationSummary, 600);
    } catch (err) {
      alert(err.message || 'Erro ao salvar tarefa.');
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await updateTaskStatus(id, newStatus, 'Interface Web');
      showToast('Status atualizado!');
      
      triggerMicroserviceToast(
        newStatus === 'COMPLETED' ? 'Tarefa Concluída!' : 'Status Alterado',
        `Microsserviço registrou novo status [${newStatus}] com sucesso via Feign.`,
        newStatus === 'COMPLETED' ? 'success' : 'info'
      );

      loadData();
      setTimeout(loadNotificationSummary, 600);
    } catch (err) {
      alert(err.message || 'Erro ao atualizar status.');
    }
  };

  const handleDeleteTask = async (id) => {
    if (window.confirm('Tem certeza de que deseja excluir esta tarefa? (Notificação de auditoria será emitida ao microsserviço)')) {
      try {
        await deleteTask(id, 'Manoel Egidio');
        showToast('Tarefa excluída!');
        triggerMicroserviceToast(
          'Alerta de Exclusão',
          `Microsserviço notification-service registrou a remoção da tarefa #${id}.`,
          'alert'
        );
        loadData();
        setTimeout(loadNotificationSummary, 600);
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

  const handleViewTaskNotifications = (task) => {
    setTaskForNotifications(task);
    setIsTaskNotificationsOpen(true);
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

  // Category filter options for CustomSelect
  const categoryFilterOptions = [
    { value: '', label: 'Todas as Categorias', dotColor: '#64748b' },
    ...categories.map((c) => ({
      value: String(c.id),
      label: c.name,
      dotColor: c.colorCode || '#6366f1'
    }))
  ];

  // Priority filter options for CustomSelect
  const priorityFilterOptions = [
    { value: '', label: 'Todas as Prioridades', dotColor: '#64748b' },
    { value: 'LOW', label: 'Baixa', dotColor: '#94a3b8' },
    { value: 'MEDIUM', label: 'Média', dotColor: '#3b82f6' },
    { value: 'HIGH', label: 'Alta', dotColor: '#f59e0b' },
    { value: 'URGENT', label: 'Urgente', dotColor: '#ef4444' }
  ];

  return (
    <div className="app-container">
      <Navbar 
        onOpenNewTaskModal={handleOpenNew} 
        onOpenNotificationCenter={() => setIsNotificationCenterOpen(true)}
        unreadNotificationCount={unreadNotificationCount}
      />

      {/* Toast flutuante de eventos do microsserviço */}
      <NotificationToast 
        toast={activeToast} 
        onClose={() => setActiveToast(null)}
        onOpenCenter={() => {
          setActiveToast(null);
          setIsNotificationCenterOpen(true);
        }}
      />

      {toastMessage && (
        <div className="toast">
          <Sparkles size={16} />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Banner de Informações de Arquitetura Distribuída TP3 */}
      <section className="hero-banner">
        <div className="hero-content">
          <div className="hero-icon-box">
            <Network size={28} />
          </div>
          <div className="hero-text-block">
            <div className="hero-title">
              TP3: Microsserviço de Notificações com Spring Cloud Feign
              <span className="hero-badge-tag">DISTRIBUÍDO</span>
            </div>
            <p className="hero-subtitle">
              Spring Boot 3.4.3 • Spring Cloud OpenFeign • Bounded Contexts • Database-per-Service (notificationdb) • Resiliência Fallback
            </p>
          </div>
        </div>

        <div className="hero-actions">
          <button 
            onClick={() => setIsNotificationCenterOpen(true)} 
            className="btn btn-secondary btn-hero-notif"
          >
            <Bell size={16} />
            <span>Central de Notificações</span>
            {unreadNotificationCount > 0 && (
              <span className="hero-unread-badge">
                {unreadNotificationCount}
              </span>
            )}
          </button>

          <button onClick={handleOpenNew} className="btn btn-primary">
            <Plus size={16} />
            <span>Nova Tarefa</span>
          </button>
        </div>
      </section>

      {/* Métricas de Performance e Domínio */}
      <section className="section-block">
        <DashboardMetrics metrics={metrics} />
      </section>

      {/* Painel de Controles, Busca e Filtros Estilizados */}
      <section className="controls-panel">
        <div className="search-bar">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Buscar tarefas por título, descrição ou categoria..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          {searchQuery && (
            <button 
              className="btn-clear-search" 
              onClick={() => setSearchQuery('')}
              title="Limpar busca"
            >
              ×
            </button>
          )}
        </div>

        <div className="filters-wrapper">
          <div className="filter-group">
            <div className="filter-group-label">
              <Filter size={14} />
              <span>Categoria:</span>
            </div>
            <CustomSelect
              value={selectedCategory}
              onChange={setSelectedCategory}
              options={categoryFilterOptions}
              placeholder="Todas as Categorias"
              className="filter-dropdown"
            />
          </div>

          <div className="filter-group">
            <div className="filter-group-label">
              <Filter size={14} />
              <span>Prioridade:</span>
            </div>
            <CustomSelect
              value={selectedPriority}
              onChange={setSelectedPriority}
              options={priorityFilterOptions}
              placeholder="Todas as Prioridades"
              className="filter-dropdown"
            />
          </div>

          <button 
            onClick={loadData} 
            className="btn btn-secondary btn-refresh" 
            title="Atualizar dados do servidor"
          >
            <RefreshCw size={15} className={loading ? 'spin' : ''} />
            <span>Atualizar</span>
          </button>
        </div>
      </section>

      {/* Status Tabs Navigation */}
      <section className="status-tabs-section">
        <div className="tabs-container">
          {[
            { id: 'ALL', label: 'Todas as Tarefas', count: tasks.length },
            { id: 'PENDING', label: 'Pendentes', count: metrics?.pendingTasks },
            { id: 'IN_PROGRESS', label: 'Em Progresso', count: metrics?.inProgressTasks },
            { id: 'COMPLETED', label: 'Concluídas', count: metrics?.completedTasks }
          ].map((tab) => (
            <button
              key={tab.id}
              className={`tab-btn ${activeStatusTab === tab.id ? 'active' : ''}`}
              onClick={() => setActiveStatusTab(tab.id)}
            >
              <span>{tab.label}</span>
              {tab.count !== undefined && (
                <span className="tab-count-pill">{tab.count}</span>
              )}
            </button>
          ))}
        </div>
      </section>

      {/* Mensagem de Erro de Conexão */}
      {error && (
        <div className="error-alert-banner">
          <AlertCircle size={24} className="error-alert-icon" />
          <div className="error-alert-content">
            <h4>Falha de Conectividade</h4>
            <p>{error}</p>
          </div>
        </div>
      )}

      {/* Grid de Tarefas */}
      <section className="tasks-grid-section">
        {loading ? (
          <div className="empty-state-card">
            <RefreshCw size={36} className="spin text-primary" />
            <h3 className="empty-state-title">Carregando tarefas...</h3>
            <p className="empty-state-subtitle">Consultando banco de dados H2 via TaskService :8080</p>
          </div>
        ) : filteredTasks.length === 0 ? (
          <div className="empty-state-card">
            <Layers size={44} className="empty-state-icon" />
            <h3 className="empty-state-title">Nenhuma tarefa encontrada</h3>
            <p className="empty-state-subtitle">
              {searchQuery || selectedCategory || selectedPriority || activeStatusTab !== 'ALL'
                ? 'Tente ajustar os filtros ou o termo de busca para visualizar os registros.'
                : 'Seu painel está limpo. Comece criando sua primeira tarefa!'}
            </p>
            <button className="btn btn-primary" onClick={handleOpenNew} style={{ marginTop: '18px' }}>
              <Plus size={16} />
              <span>Criar Nova Tarefa</span>
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
                onViewTaskNotifications={handleViewTaskNotifications}
              />
            ))}
          </div>
        )}
      </section>

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

      {/* Central de Notificações (Microsserviço notification-service: 8082) */}
      <NotificationCenter
        isOpen={isNotificationCenterOpen}
        onClose={() => setIsNotificationCenterOpen(false)}
        onNotificationCountChange={setUnreadNotificationCount}
      />

      {/* Modal de Notificações Distribuídas da Tarefa (task-service -> Feign -> notification-service) */}
      <TaskNotificationModal
        isOpen={isTaskNotificationsOpen}
        onClose={() => {
          setIsTaskNotificationsOpen(false);
          setTaskForNotifications(null);
        }}
        task={taskForNotifications}
      />
    </div>
  );
}
