import React, { useState, useEffect } from 'react';
import { 
  X, Zap, AlertOctagon, Radio, GitFork, 
  ExternalLink, CheckCircle2, RefreshCw, Trash2, Cpu
} from 'lucide-react';
import { simulateBurst, simulatePoisonPill, simulateBroadcast } from '../services/api';
import { fetchDeadLetterMessages, clearDeadLetterMessages } from '../services/notificationApi';

export default function EdaSimulatorModal({ isOpen, onClose, onRefreshData }) {
  const [burstCount, setBurstCount] = useState(15);
  const [burstLoading, setBurstLoading] = useState(false);
  const [burstResult, setBurstResult] = useState(null);

  const [poisonLoading, setPoisonLoading] = useState(false);
  const [poisonResult, setPoisonResult] = useState(null);

  const [broadcastMsg, setBroadcastMsg] = useState('Deploy de microsserviços v4.0 concluído com sucesso!');
  const [broadcastLoading, setBroadcastLoading] = useState(false);
  const [broadcastResult, setBroadcastResult] = useState(null);

  const [dlqMessages, setDlqMessages] = useState([]);
  const [dlqLoading, setDlqLoading] = useState(false);

  useEffect(() => {
    if (isOpen) {
      loadDlqMessages();
    }
  }, [isOpen]);

  const loadDlqMessages = async () => {
    setDlqLoading(true);
    try {
      const data = await fetchDeadLetterMessages();
      setDlqMessages(data || []);
    } catch {
      setDlqMessages([]);
    } finally {
      setDlqLoading(false);
    }
  };

  const handleSimulateBurst = async () => {
    setBurstLoading(true);
    setBurstResult(null);
    try {
      const res = await simulateBurst(burstCount);
      setBurstResult(res);
      if (onRefreshData) onRefreshData();
    } catch (err) {
      setBurstResult({ status: 'ERROR', message: err.message });
    } finally {
      setBurstLoading(false);
    }
  };

  const handleSimulatePoisonPill = async () => {
    setPoisonLoading(true);
    setPoisonResult(null);
    try {
      const res = await simulatePoisonPill(Date.now() % 100000);
      setPoisonResult(res);
      setTimeout(loadDlqMessages, 1500);
    } catch (err) {
      setPoisonResult({ status: 'ERROR', message: err.message });
    } finally {
      setPoisonLoading(false);
    }
  };

  const handleSimulateBroadcast = async () => {
    if (!broadcastMsg.trim()) return;
    setBroadcastLoading(true);
    setBroadcastResult(null);
    try {
      const res = await simulateBroadcast(broadcastMsg, 'Operador EDA');
      setBroadcastResult(res);
      if (onRefreshData) onRefreshData();
    } catch (err) {
      setBroadcastResult({ status: 'ERROR', message: err.message });
    } finally {
      setBroadcastLoading(false);
    }
  };

  const handleClearDlq = async () => {
    try {
      await clearDeadLetterMessages();
      setDlqMessages([]);
    } catch (err) {
      console.error(err);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content modal-distributed-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '780px' }}>
        <div className="modal-header">
          <div className="modal-title-wrap">
            <div className="modal-header-icon-box alert" style={{ background: 'rgba(99, 102, 241, 0.15)', color: '#818cf8' }}>
              <Cpu size={24} />
            </div>
            <div>
              <h2 className="modal-title">Simulador de Arquitetura Orientada a Eventos (EDA)</h2>
              <div className="distributed-route-badge" style={{ background: 'rgba(16, 185, 129, 0.12)', color: '#34d399' }}>
                <span>RabbitMQ 3.13</span>
                <span>•</span>
                <span>AMQP 0-9-1</span>
                <span>•</span>
                <span>Exchanges Direct / Topic / Fanout / DLX</span>
              </div>
            </div>
          </div>
          <button className="btn-icon-only modal-close-btn" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        <div className="modal-scroll-body" style={{ maxHeight: '600px' }}>
          {/* Topologia Banner */}
          <div className="task-summary-banner" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontWeight: 700, color: '#f8fafc', fontSize: '0.94rem' }}>
                Broker Ativo: <span style={{ color: '#10b981' }}>amqp://localhost:5672</span>
              </div>
              <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '3px' }}>
                Padrões: Event-Carried State Transfer • Event Notification • Dead Letter Exchange
              </div>
            </div>
            <a
              href="http://localhost:15672"
              target="_blank"
              rel="noreferrer"
              className="btn btn-secondary"
              style={{ fontSize: '0.78rem', padding: '6px 12px', gap: '6px' }}
            >
              <span>RabbitMQ UI</span>
              <ExternalLink size={13} />
            </a>
          </div>

          {/* Cenário 1: Alta Vazão (Burst Traffic) */}
          <div className="simulation-scenario-card">
            <div className="scenario-header">
              <div className="scenario-title">
                <Zap size={18} style={{ color: '#f59e0b' }} />
                <span>Cenário 1: Rajada Assíncrona de Eventos (Load Leveling)</span>
              </div>
              <span className="scenario-tag">DIRECT &amp; TOPIC</span>
            </div>
            <p className="scenario-desc">
              Demonstra o desacoplamento de I/O: o produtor publica dezenas de mensagens instantaneamente sem aguardar a conclusão dos consumidores.
            </p>

            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginTop: '12px' }}>
              <label style={{ fontSize: '0.84rem', color: '#cbd5e1' }}>Quantidade de eventos:</label>
              <input
                type="number"
                min="5"
                max="50"
                value={burstCount}
                onChange={(e) => setBurstCount(Number(e.target.value))}
                className="form-input"
                style={{ width: '80px', padding: '8px 12px' }}
              />
              <button
                className="btn btn-primary"
                onClick={handleSimulateBurst}
                disabled={burstLoading}
                style={{ padding: '8px 16px' }}
              >
                {burstLoading ? <RefreshCw size={15} className="spin" /> : <Zap size={15} />}
                <span>Disparar Rajada</span>
              </button>
            </div>

            {burstResult && (
              <div className="scenario-result-box">
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#10b981', fontWeight: 600 }}>
                  <CheckCircle2 size={16} />
                  <span>{burstResult.eventsPublished} eventos publicados em {burstResult.durationMs} ms!</span>
                </div>
                <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '4px' }}>
                  Tempo médio por evento: <strong>{burstResult.averageTimePerEventMs?.toFixed(2)} ms</strong> (Totalmente assíncrono).
                </div>
              </div>
            )}
          </div>

          {/* Cenário 2: Dead Letter Queue (DLQ) & Mensagens Venenosas */}
          <div className="simulation-scenario-card">
            <div className="scenario-header">
              <div className="scenario-title">
                <AlertOctagon size={18} style={{ color: '#ef4444' }} />
                <span>Cenário 2: Tratamento de Mensagens Venenosas (Dead Letter Queue - DLQ)</span>
              </div>
              <span className="scenario-tag dlx">DLX &amp; DLQ</span>
            </div>
            <p className="scenario-desc">
              Injeta uma mensagem com falha intencional. O consumidor esgota as 3 tentativas com backoff exponencial e o RabbitMQ a encaminha automaticamente para a fila de descarte (<code>task.dead-letter.queue</code>).
            </p>

            <div style={{ marginTop: '12px', display: 'flex', gap: '12px' }}>
              <button
                className="btn btn-danger"
                onClick={handleSimulatePoisonPill}
                disabled={poisonLoading}
                style={{ padding: '8px 16px' }}
              >
                {poisonLoading ? <RefreshCw size={15} className="spin" /> : <AlertOctagon size={15} />}
                <span>Injetar Poison Pill na Fila</span>
              </button>

              <button
                className="btn btn-secondary"
                onClick={loadDlqMessages}
                disabled={dlqLoading}
                style={{ padding: '8px 14px' }}
              >
                <RefreshCw size={14} className={dlqLoading ? 'spin' : ''} />
                <span>Atualizar DLQ ({dlqMessages.length})</span>
              </button>

              {dlqMessages.length > 0 && (
                <button
                  className="btn btn-secondary"
                  onClick={handleClearDlq}
                  style={{ padding: '8px 14px', color: '#ef4444' }}
                >
                  <Trash2 size={14} />
                  <span>Limpar DLQ</span>
                </button>
              )}
            </div>

            {poisonResult && (
              <div className="scenario-result-box" style={{ borderColor: 'rgba(239, 68, 68, 0.3)' }}>
                <div style={{ color: '#f87171', fontWeight: 600, fontSize: '0.85rem' }}>
                  {poisonResult.expectedBehavior}
                </div>
              </div>
            )}

            {/* Listagem de mensagens na DLQ */}
            {dlqMessages.length > 0 && (
              <div style={{ marginTop: '14px', background: 'rgba(0, 0, 0, 0.4)', borderRadius: '10px', padding: '12px' }}>
                <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#f87171', textTransform: 'uppercase', marginBottom: '8px' }}>
                  Mensagens Capturadas na Dead Letter Queue ({dlqMessages.length}):
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {dlqMessages.map((msg, i) => (
                    <div key={i} style={{ fontSize: '0.78rem', color: '#cbd5e1', background: 'rgba(239, 68, 68, 0.1)', padding: '8px 10px', borderRadius: '6px', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
                      <strong>ID: {msg.eventId}</strong> • Tarefa #{msg.taskId} • Tipo: {msg.eventType}
                      <div style={{ color: '#94a3b8', marginTop: '2px' }}>{msg.description}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Cenário 3: Broadcast Geral via Fanout Exchange */}
          <div className="simulation-scenario-card">
            <div className="scenario-header">
              <div className="scenario-title">
                <Radio size={18} style={{ color: '#38bdf8' }} />
                <span>Cenário 3: Disparo de Alerta Geral (Fanout Exchange / Pub-Sub)</span>
              </div>
              <span className="scenario-tag fanout">FANOUT</span>
            </div>
            <p className="scenario-desc">
              O evento é propagado para todas as filas vinculadas à FanoutExchange sem necessidade de routing keys.
            </p>

            <div style={{ display: 'flex', gap: '10px', marginTop: '12px' }}>
              <input
                type="text"
                value={broadcastMsg}
                onChange={(e) => setBroadcastMsg(e.target.value)}
                className="form-input"
                placeholder="Mensagem de alerta..."
                style={{ flex: 1, padding: '8px 12px' }}
              />
              <button
                className="btn btn-primary"
                onClick={handleSimulateBroadcast}
                disabled={broadcastLoading || !broadcastMsg.trim()}
                style={{ padding: '8px 16px' }}
              >
                {broadcastLoading ? <RefreshCw size={15} className="spin" /> : <GitFork size={15} />}
                <span>Disparar Fanout</span>
              </button>
            </div>

            {broadcastResult && (
              <div className="scenario-result-box" style={{ borderColor: 'rgba(56, 189, 248, 0.3)' }}>
                <div style={{ color: '#38bdf8', fontWeight: 600, fontSize: '0.85rem' }}>
                  Alerta Fanout emitido com sucesso para a fila <code>task.broadcast.queue</code>!
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="modal-actions" style={{ marginTop: '18px' }}>
          <button className="btn btn-secondary" onClick={onClose}>
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
