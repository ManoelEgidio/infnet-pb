import React, { useState, useEffect } from 'react';
import { 
  Activity, 
  Server, 
  Cpu, 
  ExternalLink, 
  CheckCircle2, 
  AlertTriangle, 
  RefreshCw, 
  X, 
  Terminal, 
  Layers, 
  ShieldCheck, 
  GitBranch,
  BarChart3
} from 'lucide-react';

export default function ObservabilityModal({ isOpen, onClose }) {
  const [taskLiveness, setTaskLiveness] = useState(null);
  const [taskReadiness, setTaskReadiness] = useState(null);
  const [notifLiveness, setNotifLiveness] = useState(null);
  const [notifReadiness, setNotifReadiness] = useState(null);
  const [prometheusStatus, setPrometheusStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchProbes = async () => {
    setLoading(true);
    // Task Service Probes
    try {
      const resLive = await fetch('http://localhost:8080/actuator/health/liveness', { signal: AbortSignal.timeout(2000) });
      const dataLive = await resLive.json();
      setTaskLiveness(dataLive.status === 'UP');
    } catch {
      setTaskLiveness(false);
    }

    try {
      const resReady = await fetch('http://localhost:8080/actuator/health/readiness', { signal: AbortSignal.timeout(2000) });
      const dataReady = await resReady.json();
      setTaskReadiness(dataReady.status === 'UP');
    } catch {
      setTaskReadiness(false);
    }

    // Notification Service Probes
    try {
      const resLive = await fetch('http://localhost:8082/actuator/health/liveness', { signal: AbortSignal.timeout(2000) });
      const dataLive = await resLive.json();
      setNotifLiveness(dataLive.status === 'UP');
    } catch {
      setNotifLiveness(false);
    }

    try {
      const resReady = await fetch('http://localhost:8082/actuator/health/readiness', { signal: AbortSignal.timeout(2000) });
      const dataReady = await resReady.json();
      setNotifReadiness(dataReady.status === 'UP');
    } catch {
      setNotifReadiness(false);
    }

    // Prometheus Endpoint Check
    try {
      const resProm = await fetch('http://localhost:8080/actuator/prometheus', { signal: AbortSignal.timeout(2000) });
      setPrometheusStatus(resProm.ok);
    } catch {
      setPrometheusStatus(false);
    }

    setLoading(false);
  };

  useEffect(() => {
    if (isOpen) {
      fetchProbes();
    }
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content observability-modal" onClick={e => e.stopPropagation()} style={{ maxWidth: '820px' }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div className="badge-eda" style={{ background: 'rgba(59, 130, 246, 0.2)', color: '#60a5fa' }}>
              <Activity size={18} />
            </div>
            <div>
              <h2 className="modal-title">Painel de Observabilidade & Produção (TP5)</h2>
              <p className="modal-subtitle">Monitoramento de Probes Kubernetes, Prometheus, Zipkin Tracing e CI/CD</p>
            </div>
          </div>
          <button className="btn-close" onClick={onClose} aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          
          {/* Top Info Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px' }}>
            <div className="card-topology" style={{ padding: '14px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                <Layers size={16} style={{ color: '#38bdf8' }} />
                <span style={{ fontSize: '0.82rem', fontWeight: 600, color: '#94a3b8' }}>Namespace K8s</span>
              </div>
              <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f8fafc' }}>taskmanager-prod</div>
              <span style={{ fontSize: '0.75rem', color: '#64748b' }}>Isolamento Multi-Tenant</span>
            </div>

            <div className="card-topology" style={{ padding: '14px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                <Cpu size={16} style={{ color: '#a855f7' }} />
                <span style={{ fontSize: '0.82rem', fontWeight: 600, color: '#94a3b8' }}>Políticas HPA</span>
              </div>
              <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f8fafc' }}>2 a 10 Pods (CPU 70%)</div>
              <span style={{ fontSize: '0.75rem', color: '#64748b' }}>Escalabilidade Automática</span>
            </div>

            <div className="card-topology" style={{ padding: '14px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                <GitBranch size={16} style={{ color: '#22c55e' }} />
                <span style={{ fontSize: '0.82rem', fontWeight: 600, color: '#94a3b8' }}>Pipeline CI/CD</span>
              </div>
              <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f8fafc' }}>GitHub Actions</div>
              <span style={{ fontSize: '0.75rem', color: '#22c55e' }}>Lint + Test + Build + K8s CD</span>
            </div>
          </div>

          {/* Kubernetes Probes Status */}
          <div style={{ background: 'rgba(30, 41, 59, 0.7)', borderRadius: '12px', padding: '16px', border: '1px solid rgba(255, 255, 255, 0.08)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 600, color: '#f8fafc', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <ShieldCheck size={18} style={{ color: '#38bdf8' }} />
                Status das Probes Kubernetes (Spring Boot Actuator)
              </h3>
              <button 
                className="btn btn-secondary" 
                onClick={fetchProbes} 
                disabled={loading}
                style={{ padding: '4px 10px', fontSize: '0.8rem', gap: '5px' }}
              >
                <RefreshCw size={13} className={loading ? 'spin' : ''} />
                <span>Atualizar Probes</span>
              </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
              {/* Task Service Probes */}
              <div style={{ background: 'rgba(15, 23, 42, 0.6)', borderRadius: '8px', padding: '12px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
                  <Server size={15} style={{ color: '#38bdf8' }} />
                  <span style={{ fontWeight: 600, fontSize: '0.88rem', color: '#e2e8f0' }}>Task-Service (:8080)</span>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.82rem' }}>
                    <span style={{ color: '#94a3b8' }}>Liveness Probe (/actuator/health/liveness):</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '5px', fontWeight: 600, color: taskLiveness ? '#22c55e' : '#ef4444' }}>
                      {taskLiveness ? <CheckCircle2 size={14} /> : <AlertTriangle size={14} />}
                      {taskLiveness ? 'UP (Saudável)' : 'OFFLINE'}
                    </span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.82rem' }}>
                    <span style={{ color: '#94a3b8' }}>Readiness Probe (/actuator/health/readiness):</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '5px', fontWeight: 600, color: taskReadiness ? '#22c55e' : '#ef4444' }}>
                      {taskReadiness ? <CheckCircle2 size={14} /> : <AlertTriangle size={14} />}
                      {taskReadiness ? 'UP (Pronto p/ Tráfego)' : 'OFFLINE'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Notification Service Probes */}
              <div style={{ background: 'rgba(15, 23, 42, 0.6)', borderRadius: '8px', padding: '12px', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
                  <Activity size={15} style={{ color: '#818cf8' }} />
                  <span style={{ fontWeight: 600, fontSize: '0.88rem', color: '#e2e8f0' }}>Notification-Service (:8082)</span>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.82rem' }}>
                    <span style={{ color: '#94a3b8' }}>Liveness Probe (/actuator/health/liveness):</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '5px', fontWeight: 600, color: notifLiveness ? '#22c55e' : '#ef4444' }}>
                      {notifLiveness ? <CheckCircle2 size={14} /> : <AlertTriangle size={14} />}
                      {notifLiveness ? 'UP (Saudável)' : 'OFFLINE'}
                    </span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.82rem' }}>
                    <span style={{ color: '#94a3b8' }}>Readiness Probe (/actuator/health/readiness):</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '5px', fontWeight: 600, color: notifReadiness ? '#22c55e' : '#ef4444' }}>
                      {notifReadiness ? <CheckCircle2 size={14} /> : <AlertTriangle size={14} />}
                      {notifReadiness ? 'UP (Pronto p/ Tráfego)' : 'OFFLINE'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Observability Tools Quick Links */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '14px' }}>
            {/* Prometheus Card */}
            <div style={{ background: 'rgba(30, 41, 59, 0.7)', borderRadius: '12px', padding: '16px', border: '1px solid rgba(255, 255, 255, 0.08)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <BarChart3 size={18} style={{ color: '#f59e0b' }} />
                  <span style={{ fontWeight: 600, color: '#f8fafc' }}>Métricas Prometheus</span>
                  <span style={{ 
                    fontSize: '0.72rem', 
                    padding: '2px 8px', 
                    borderRadius: '10px', 
                    fontWeight: 600,
                    background: prometheusStatus ? 'rgba(34, 197, 94, 0.2)' : 'rgba(239, 68, 68, 0.2)',
                    color: prometheusStatus ? '#4ade80' : '#f87171'
                  }}>
                    {prometheusStatus ? 'Scraping Ativo' : 'Offline'}
                  </span>
                </div>
                <a 
                  href="http://localhost:8080/actuator/prometheus" 
                  target="_blank" 
                  rel="noreferrer" 
                  className="btn btn-secondary"
                  style={{ padding: '4px 10px', fontSize: '0.78rem', gap: '4px' }}
                >
                  <span>Scrape Endpoint</span>
                  <ExternalLink size={12} />
                </a>
              </div>
              <p style={{ fontSize: '0.82rem', color: '#94a3b8', margin: 0 }}>
                Exporta métricas de JVM, latência HTTP, contadores e filas RabbitMQ no padrão aberto de telemetria.
              </p>
            </div>

            {/* Zipkin Tracing Card */}
            <div style={{ background: 'rgba(30, 41, 59, 0.7)', borderRadius: '12px', padding: '16px', border: '1px solid rgba(255, 255, 255, 0.08)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Terminal size={18} style={{ color: '#ec4899' }} />
                  <span style={{ fontWeight: 600, color: '#f8fafc' }}>Rastreamento Zipkin</span>
                </div>
                <a 
                  href="http://localhost:9411" 
                  target="_blank" 
                  rel="noreferrer" 
                  className="btn btn-secondary"
                  style={{ padding: '4px 10px', fontSize: '0.78rem', gap: '4px' }}
                >
                  <span>Painel Zipkin</span>
                  <ExternalLink size={12} />
                </a>
              </div>
              <p style={{ fontSize: '0.82rem', color: '#94a3b8', margin: 0 }}>
                Rastreamento distribuído de transações assíncronas (Trace ID & Span ID propagados via RabbitMQ AMQP headers).
              </p>
            </div>
          </div>

        </div>

        <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.82rem', color: '#64748b' }}>
            <span style={{ display: 'inline-block', width: '8px', height: '8px', borderRadius: '50%', background: '#22c55e' }}></span>
            <span>Ambiente Preparado para Produção: Dockerfile Multi-Stage + Kubernetes K8s + CI/CD Actions</span>
          </div>
          <button className="btn btn-secondary" onClick={onClose}>Fechar</button>
        </div>
      </div>
    </div>
  );
}
