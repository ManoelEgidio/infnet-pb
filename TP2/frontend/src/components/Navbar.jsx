import React from 'react';
import { Layers, Plus, Server } from 'lucide-react';

export default function Navbar({ onOpenNewTaskModal }) {
  return (
    <header className="navbar">
      <div className="brand">
        <div className="brand-icon">
          <Layers size={24} />
        </div>
        <div>
          <h1 className="brand-title">TaskManager PB</h1>
          <p className="brand-subtitle">TP1 Monólito Spring Boot + React</p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.8rem', color: '#10b981', background: 'rgba(16,185,129,0.1)', padding: '6px 12px', borderRadius: '20px', border: '1px solid rgba(16,185,129,0.2)' }}>
          <Server size={14} />
          <span>Spring Boot REST Active</span>
        </div>

        <button className="btn btn-primary" onClick={onOpenNewTaskModal}>
          <Plus size={18} />
          <span>Nova Tarefa</span>
        </button>
      </div>
    </header>
  );
}
